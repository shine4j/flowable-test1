package com.ctgu;

/**
 * @Author beck_guo
 * @create 2022/6/9 13:04
 * @description
 */
public class Atest {
    CREATE OR REPLACE PROCEDURE New_Sign_Flow (
            VDOC_KIND      IN   VARCHAR2,
            VNO            IN   VARCHAR2,
            VSUBJECT       IN   VARCHAR2,
            VEG_LEV        IN   VARCHAR2,
            VAP_EMPNO      IN   VARCHAR2,
            VCREATE_BY     IN   VARCHAR2,
            IN_LEV         IN   VARCHAR2,
            START_SEQ      IN   NUMBER DEFAULT 1,
            VATTACH        IN   VARCHAR2 DEFAULT 'N',
            VGRADE_COUNT   IN   VARCHAR2 DEFAULT 1,
            VGRADE_LIMIT   IN   VARCHAR2,            ----簽直屬主管時主管級別的最大限制
                    V_BATCH_CODE   IN   VARCHAR2
                    DEFAULT NULL        -- add by jianliang.cai 2010/09/27 for batch sign
    )
--- Cory.Liu 2007/09/26  Refine
--- Cory.Liu 2007/10/25  Update last sige dept grade to v_lev for impower issue
--- Cory.Liu 2007/11/08  Add with sigh(approval) and novice function
--- Cory.Liu 2007/11/28  Add Co-Header Rule
--- Cory.Liu 2008/06/04  Update Co-Header Rule : Add excluding condition when ap_empno = sign.boss
--- Kevin.lau 2010/12/23 Add check doc_kind for with sign
--- Cory.Liu 2011/07/11 Update for hr employee master project
--- This procedure only called by apv_sign_forward, do not call it directly
--- Program Flow
--- 1. Check organization data
--- 2. organization level sign (loop)
--- 3. with sign (loop loop)
--- 4. Last sign record check
    IS
    CURSOR C_ORG (X_DEPTNO IN VARCHAR2)
    IS
    SELECT     PARENT, CHILD
    FROM PS_ORGANIZATION_OPEN_V D
    CONNECT BY PRIOR D.PARENT = D.CHILD
    START WITH CHILD = X_DEPTNO;

    V_TMP_SEQ                    NUMBER (6)                     := START_SEQ;
    V_LEV                        VARCHAR2 (10);
    V_DEPTNO                     PS_EMPLOYEE_ALL.DEPTNO%TYPE; --VARCHAR2 (10);
    V_BOSS                       PS_DEPARTMENT.BOSS%TYPE;     --VARCHAR2 (10);
    V_PRIV                       PS_DEPARTMENT.SIGN_PRIV%TYPE;
    V_GRADE                      PS_DEPARTMENT.GRADE%TYPE;   --NUMBER (10, 3);
    V_COUNT                      NUMBER                         := 0;
    V_PRE_DEPTNO                 PS_DEPARTMENT.DEPTNO%TYPE     := 'PREDEPT#$@';
    V_SIGN_FL                    SIGN_FL%ROWTYPE;
    V_BA_FLAG                    MICPO_WITH_SIGN.BA_FLAG%TYPE;
    V_UNDER_LEVEL_FLAG           VARCHAR2 (1)                   := 'N';
    V_GRADE_COUNTER              NUMBER (6)                     := START_SEQ;
   --- Counter for grade sign
   ---- for co-header rule
    V_CO_HEADER_FLAG             VARCHAR2 (1)                   := 'Y';
    V_CO_HEADER_EXCLUDING_FLAG   VARCHAR2 (1)                   := 'N';
    V_HIGHEST_CO_HEADER_FLAG     VARCHAR2 (1)                   := 'Y';
    V_REAL_DEPTGRADE             PS_DEPARTMENT.GRADE%TYPE;
    V_PRE_REAL_DEPTGRADE         PS_DEPARTMENT.GRADE%TYPE       := '-1';
    V_CURRENT_LEV_SEQ            NUMBER (6);

   ---- Save the current level normal sign seq
    PROCEDURE CHECK_ORGANIZATION_DATA (X_DEPTNO VARCHAR2)
    IS
    V_TEMP_DEPT   PS_ORGANIZATION.PARENT%TYPE;
    BEGIN
    FOR RX_ORG IN C_ORG (X_DEPTNO)
    LOOP
    V_TEMP_DEPT := RX_ORG.PARENT;
    END LOOP;

    IF V_TEMP_DEPT = 'S'
    THEN
            NULL;
    ELSE
    RAISE_APPLICATION_ERROR
            (-20010,
                     '抱歉,'
                     || X_DEPTNO
             || ':此部門父級組織架構資料不完善，請聯系人事或IT，單據暫時不能送簽。'
            );
    END IF;
    END;

    PROCEDURE GET_DEPT_INFORMATION (
            P_DEPTNO      IN       PS_DEPARTMENT.DEPTNO%TYPE,
            P_BOSS        OUT      PS_DEPARTMENT.BOSS%TYPE,
            P_SIGN_PRIV   OUT      PS_DEPARTMENT.SIGN_PRIV%TYPE,
            P_GRADE       OUT      PS_DEPARTMENT.GRADE%TYPE
    )
    IS
            BEGIN
    SELECT BOSS, SIGN_PRIV, GRADE
    INTO P_BOSS, P_SIGN_PRIV, P_GRADE
    FROM PS_DEPARTMENT
    WHERE DEPTNO = P_DEPTNO
    AND NVL (CLOSE_DATE, SYSDATE + 1) > TRUNC (SYSDATE);
    EXCEPTION
    WHEN NO_DATA_FOUND
    THEN
    RAISE_APPLICATION_ERROR (-20012,
                                     'Get boss,sign_priv,grade Error'
                                     || P_DEPTNO
                                  || ':'
                                     || SQLERRM
    );
    END;

    PROCEDURE INSERT_WITH_SIGN (
            X_PARENT_DEPTNO   VARCHAR2,
            X_CHILD_DEPTNO    VARCHAR2,
            X_BA_FLAG         VARCHAR2,
            X_DEPT_GRADE      VARCHAR2,
            X_BATCH_CODE      VARCHAR2 DEFAULT NULL
    )
    IS
    CURSOR C_WITH_SIGN (
            X_PARENT_DEPTNO   VARCHAR2,
            X_CHILD_DEPTNO    VARCHAR2,
            X_BA_FLAG         VARCHAR2
    )
    IS
    SELECT *
    FROM MICPO_WITH_SIGN MW
    WHERE MW.PARENT_DEPTNO = X_PARENT_DEPTNO
    AND MW.CHILD_DEPTNO = X_CHILD_DEPTNO
    AND BA_FLAG = X_BA_FLAG
    AND (TRUNC (SYSDATE) BETWEEN NVL (EFFECT_START_DATE, SYSDATE - 1)
    AND NVL (EFFECT_END_DATE, SYSDATE + 1)
                )
                        ---modify by jery.zhang on 2009/03/10 Check effect_start_date
    AND MICPO_IS_AGENT_EXCLUSION_FLAG(MW.AGENT_LINE_ID,
                                      MW.CONSTRAINT_TYPE,
                                      VDOC_KIND) = 'N';
                  ---modify by kevin.lau on 2010/12/23 Check doc_kind
   --- and (sysdate between effect_start_date and nvl(effect_end_date,sysdate+1)); ---add by jery.zhang on 2009/03/06  Check valide date
            BEGIN
    FOR R_WS_NOVICE IN C_WITH_SIGN (X_PARENT_DEPTNO,
                                    X_CHILD_DEPTNO,
                                    X_BA_FLAG
                                    )
    LOOP
    Micpo_Insert_Sign_Fl (X_SEQ              => TO_CHAR (V_TMP_SEQ),
    X_NO               => VNO,
    X_EMPNO            => R_WS_NOVICE.SIGN_EMPNO,
    X_AP_EMPNO         => VAP_EMPNO,
    X_SUBJECT          =>    VSUBJECT
                                                     || '('
                                                             || X_BA_FLAG
                                                     || ')',
    X_ATTACH           => VATTACH,
    X_EG_LEV           => VEG_LEV,
    X_DOC_KIND         => VDOC_KIND,
    X_CREATE_BY        => VCREATE_BY,
    X_DEPT_GRADE       => NULL,
    X_SIGN_DEPTNO      => NULL,
    X_BATCH_CODE       => V_BATCH_CODE
                              );

    IF X_BA_FLAG != 'Corporate'
    THEN
    V_TMP_SEQ := V_TMP_SEQ + 1;
    END IF;
    END LOOP;
    END;

    FUNCTION GET_CO_HEADER_ENABLE
    RETURN VARCHAR2
    IS
    V_CO_HEADER_ENABLE   VARCHAR2 (1);
    BEGIN
    SELECT SUBSTRB (VALUE, 1, 1)
    INTO V_CO_HEADER_ENABLE
    FROM SPECIAL_SETTING
    WHERE TYPE = 'CO_HEADER_SIGN' AND CODE = 'ENABLE' AND ROWNUM = 1;

    RETURN V_CO_HEADER_ENABLE;
    EXCEPTION
    WHEN OTHERS
    THEN
    RETURN NULL;
    END;

    FUNCTION IS_CO_HEADER_FLAG (
            P_DEPTNO                 IN       VARCHAR2,
            P_GRADE                  IN       VARCHAR2,
            X_IS_HIGHEST_CO_HEADER   OUT      VARCHAR2
    )
    RETURN VARCHAR2
    IS
    V_IS_CO_HEADER_FLAG     VARCHAR2 (1) := 'N';
    V_EXISTS_CO_HEADERS_P   NUMBER       := 0;
    V_EXISTS_CO_HEADERS_C   NUMBER       := 0;
    BEGIN
    IF NVL (GET_CO_HEADER_ENABLE, 'N') <> 'Y'
    THEN
    V_IS_CO_HEADER_FLAG := 'N';
    ELSE
    SELECT COUNT ('X')
    INTO V_EXISTS_CO_HEADERS_P
    FROM PS_ORGANIZATION_OPEN_V POO, PS_DEPARTMENT PD
    WHERE POO.CHILD = P_DEPTNO
    AND PD.DEPTNO = POO.PARENT
    AND NVL (PD.CLOSE_DATE, SYSDATE + 1) > TRUNC (SYSDATE)
    AND PD.GRADE = P_GRADE;

    SELECT COUNT ('X')
    INTO V_EXISTS_CO_HEADERS_C
    FROM PS_ORGANIZATION_OPEN_V POO, PS_DEPARTMENT PD
    WHERE POO.PARENT = P_DEPTNO
    AND PD.DEPTNO = POO.CHILD
    AND NVL (PD.CLOSE_DATE, SYSDATE + 1) > TRUNC (SYSDATE)
    AND PD.GRADE = P_GRADE;

    IF V_EXISTS_CO_HEADERS_P > 0 OR V_EXISTS_CO_HEADERS_C > 0
    THEN
    V_IS_CO_HEADER_FLAG := 'Y';
    END IF;
    END IF;

    IF V_EXISTS_CO_HEADERS_P = 0 AND V_IS_CO_HEADER_FLAG = 'Y'
    THEN
    X_IS_HIGHEST_CO_HEADER := 'Y';
    ELSE
    X_IS_HIGHEST_CO_HEADER := 'N';
    END IF;

    RETURN V_IS_CO_HEADER_FLAG;
    END;

    FUNCTION IS_CO_HEADER_EXCLUDING (
            P_AP_EMPNO      IN   VARCHAR2,
            P_SIGN_EMPNO    IN   VARCHAR2,
            P_AP_DEPTNO     IN   VARCHAR2,
            P_SIGN_DEPTNO   IN   VARCHAR2
    )
    RETURN VARCHAR2
    IS
    V_CO_HEADER_EXCLUDING   VARCHAR2 (1) := 'N';
    V_EXISTS_MANAGE_DEPT    NUMBER       := 0;
    BEGIN
    IF P_AP_EMPNO = '00171' AND P_SIGN_EMPNO = '00171' --modified by claudia from 00651 to 00171   20110808
    THEN
    V_CO_HEADER_EXCLUDING := 'N';
    ELSIF P_AP_DEPTNO = P_SIGN_DEPTNO
    THEN
    V_CO_HEADER_EXCLUDING := 'Y';
    ELSIF P_AP_EMPNO = P_SIGN_EMPNO
    THEN
    V_CO_HEADER_EXCLUDING := 'Y';
    ELSE
    SELECT COUNT ('X')
    INTO V_EXISTS_MANAGE_DEPT
    FROM PS_ORGANIZATION_OPEN_V POO, PS_DEPARTMENT PD
    WHERE POO.PARENT = P_SIGN_DEPTNO
    AND PD.DEPTNO = POO.CHILD
    AND NVL (PD.CLOSE_DATE, SYSDATE + 1) > TRUNC (SYSDATE)
    AND PD.CHINESE_NAME LIKE '%管理室%'
            --AND pd.grade IN (4, 6)
    AND POO.CHILD = P_AP_DEPTNO;

    IF V_EXISTS_MANAGE_DEPT > 0
    THEN
    V_CO_HEADER_EXCLUDING := 'Y';
    END IF;
    END IF;

    RETURN V_CO_HEADER_EXCLUDING;
    END;

    BEGIN
    IF UPPER (IN_LEV) = 'N'
    THEN                                                        --表示直屬主管
      --v_lev := '1';
    V_LEV := NVL (VGRADE_LIMIT, 1);
    ELSE
    V_LEV := IN_LEV;
    END IF;

  -- SPECIAL_FOR_MTC_TOP_BOSS1;

   --- 1.1 Check employee data
            BEGIN
    SELECT DEPTNO
    INTO V_DEPTNO
    FROM PS_EMPLOYEE
    WHERE EMPNO = VAP_EMPNO;
    EXCEPTION
    WHEN NO_DATA_FOUND
    THEN
    RAISE_APPLICATION_ERROR (-20011,
                             VAP_EMPNO || ': No employee data found.'
    );
    END;

   ----1.2 Check organization data
    CHECK_ORGANIZATION_DATA (V_DEPTNO);

   --- 2. organization level sign (loop)
    FOR R_ORG IN C_ORG (V_DEPTNO)
    LOOP
    GET_DEPT_INFORMATION (R_ORG.CHILD, V_BOSS, V_PRIV, V_GRADE);
    V_REAL_DEPTGRADE := V_GRADE;
    V_CO_HEADER_FLAG :=
    IS_CO_HEADER_FLAG (R_ORG.CHILD,
                       V_REAL_DEPTGRADE,
                       V_HIGHEST_CO_HEADER_FLAG
                       );
      DBMS_OUTPUT.PUT_LINE (   V_TMP_SEQ
                            || ':'
                                    || V_BOSS
                            || ':'
                                    || V_CO_HEADER_FLAG
                            || ':'
                                    || V_CO_HEADER_EXCLUDING_FLAG
                            || ':'
                                    || V_REAL_DEPTGRADE
                            || ':'
                                    || V_PRE_REAL_DEPTGRADE
                            || ':'
                                    || V_HIGHEST_CO_HEADER_FLAG
                           );

    IF     V_PRIV = 1
            -- AND (v_boss <> vap_empno or r_org.parent = 'S')
    AND NOT (    V_CO_HEADER_FLAG = 'Y'
            AND V_CO_HEADER_EXCLUDING_FLAG = 'Y'
            AND V_REAL_DEPTGRADE = V_PRE_REAL_DEPTGRADE
    )
    THEN
    IF TO_NUMBER (V_GRADE) < TO_NUMBER (V_LEV)
    THEN
    V_GRADE := V_LEV;
         -- v_under_level_flag := 'Y';
    ELSE
    V_UNDER_LEVEL_FLAG := 'N';
    END IF;

         ---- Co-header seq
    IF     V_CO_HEADER_FLAG = 'Y'
    AND V_CO_HEADER_EXCLUDING_FLAG = 'N'
    AND V_REAL_DEPTGRADE = V_PRE_REAL_DEPTGRADE
    THEN
    V_TMP_SEQ := NVL (V_CURRENT_LEV_SEQ, V_TMP_SEQ);
    END IF;

    IF V_UNDER_LEVEL_FLAG != 'Y'
    THEN
            --- Insert Novice with sign records
    V_BA_FLAG := 'Novice';

    IF  v_boss<>VAP_EMPNO then   --當申請人不是該部門的BOSS的時候才加簽
    INSERT_WITH_SIGN (R_ORG.CHILD,
                      R_ORG.CHILD,
                      V_BA_FLAG,
                      V_GRADE,
                      V_BATCH_CODE
                      );
    INSERT_WITH_SIGN (R_ORG.CHILD,
                      V_PRE_DEPTNO,
                      V_BA_FLAG,
                      V_GRADE,
                      V_BATCH_CODE
                      );
    end IF;
            --- Insert Novice with sign records
    V_BA_FLAG := 'Corporate';
    INSERT_WITH_SIGN (R_ORG.CHILD,
                      R_ORG.CHILD,
                      V_BA_FLAG,
                      V_GRADE,
                      V_BATCH_CODE
                      );
    INSERT_WITH_SIGN (R_ORG.CHILD,
                      V_PRE_DEPTNO,
                      V_BA_FLAG,
                      V_GRADE,
                      V_BATCH_CODE
                      );
    END IF;

         --- Normal sign
    IF V_BOSS <> VAP_EMPNO OR R_ORG.PARENT = 'S'
    THEN
    Micpo_Insert_Sign_Fl (X_SEQ              => TO_CHAR (V_TMP_SEQ),
    X_NO               => VNO,
    X_EMPNO            => V_BOSS,
    X_AP_EMPNO         => VAP_EMPNO,
    X_SUBJECT          => VSUBJECT,
    X_ATTACH           => VATTACH,
    X_EG_LEV           => VEG_LEV,
    X_DOC_KIND         => VDOC_KIND,
    X_CREATE_BY        => VCREATE_BY,
    X_DEPT_GRADE       => V_GRADE,
    X_SIGN_DEPTNO      => R_ORG.CHILD,
    X_BATCH_CODE       => V_BATCH_CODE
                                 );
    V_CURRENT_LEV_SEQ := V_TMP_SEQ;
    V_TMP_SEQ := V_TMP_SEQ + 1;
    V_GRADE_COUNTER := V_GRADE_COUNTER + 1;
    END IF;

    IF V_UNDER_LEVEL_FLAG != 'Y'
    THEN
            ----- Insert Approval with sign records
    V_BA_FLAG := 'Approval';
    INSERT_WITH_SIGN (R_ORG.CHILD,
                      R_ORG.CHILD,
                      V_BA_FLAG,
                      V_GRADE,
                      V_BATCH_CODE
                      );
    INSERT_WITH_SIGN (R_ORG.CHILD,
                      V_PRE_DEPTNO,
                      V_BA_FLAG,
                      V_GRADE,
                      V_BATCH_CODE
                      );
    END IF;

         --- Check leave loop
         --- Co-Header Rule
    IF V_BOSS <> VAP_EMPNO OR R_ORG.PARENT = 'S'
    THEN
    IF (    IN_LEV = 'N'
            AND V_GRADE_COUNTER >= START_SEQ + NVL (VGRADE_COUNT, 1)
               )
    THEN
            EXIT;
    ELSIF V_LEV = V_GRADE AND V_CO_HEADER_FLAG != 'Y'
    THEN
            EXIT;
    ELSIF     TO_NUMBER (V_LEV) < TO_NUMBER (V_GRADE)
    AND V_CO_HEADER_FLAG != 'Y'
    THEN
            NULL;
    ELSIF     V_LEV = V_GRADE
    AND V_CO_HEADER_FLAG = 'Y'
    AND V_HIGHEST_CO_HEADER_FLAG = 'Y'
    THEN
            EXIT;
    END IF;
    END IF;                            ---add by jery.zhang on 2010/05/31
    ELSE
         DBMS_OUTPUT.PUT_LINE
                 (   'Department have no sign priv or boss = ap_empno or co-header skip:'
                 || R_ORG.CHILD
            );
    END IF;

    V_CO_HEADER_EXCLUDING_FLAG :=
    IS_CO_HEADER_EXCLUDING (VAP_EMPNO, V_BOSS, V_DEPTNO, R_ORG.CHILD);

      ---- Exit for co-header
    IF     TO_NUMBER (V_LEV) >= TO_NUMBER (V_GRADE)
    AND V_CO_HEADER_FLAG = 'Y'
    AND V_HIGHEST_CO_HEADER_FLAG = 'Y'
    AND V_TMP_SEQ > START_SEQ
            THEN
    EXIT;
    END IF;

      -- Save deptno into pre_deptno , deptgrade into pre_real_deptgrade
    V_PRE_DEPTNO := R_ORG.CHILD;
    V_PRE_REAL_DEPTGRADE := V_REAL_DEPTGRADE;
    END LOOP;

   --- 3.5
           --SPECIAL_FOR_MTC_TOP_BOSS2;

   --- 4. Last sign record check
   ---- Get sign flow count
    BEGIN
    SELECT COUNT (*)
    INTO V_COUNT
    FROM SIGN_FL_OPEN SF
    WHERE SF.DOC_KIND = VDOC_KIND AND SF.NO = VNO;      ---- Open Sign_flow

    IF V_COUNT = 0
    THEN
    RAISE_APPLICATION_ERROR
            (-20013,
                     'New sign flow have no records. Please check with MIS.'
            );
    END IF;
    END;
    END;

}


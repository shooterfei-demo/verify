package com.example.word;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class ResumeInfo {

    @Data
    public static class ConsultantEvaluation {
        /**
         * 离职原因
         */
        private String dimissionReason;
        /**
         * 看机会原因
         */
        private String chanceReason;
    }


    @Data
    public static class BaseInfo {
        /**
         * 姓名
         */
        private String name;
        /**
         * 年龄
         */
        private String age;
        /**
         * 性别
         */
        private String sex;
        /**
         * 工作年限
         */
        private String seniority;
        /**
         * 当前状态
         */
        private String workStatus;
        /**
         * 当前年薪
         */
        private String annualSalary;

        /**
         * 期望年薪
         */
        private String expectedAnnualSalary;

        /**
         * 政治面貌
         */
        private String politicalStatus;

        /**
         * 婚姻状况
         */
        private String maritalStatus;

        /**
         * 目前所在地
         */
        private String presentLocation;

        /**
         * 期望所在地
         */
        private String desiredLocation;

        /**
         * 到岗时间
         */
        private String arrivalTime;

        /**
         * 求职动机
         */
        private String jobSeekingMotivation;

        /**
         * 电话
         */
        private String phone;

        /**
         * 邮箱
         */
        private String email;

    }

    @Data
    public static class EducationInfo {
        /**
         * 时间
         */
        private String timeArrange;

        /**
         * 学校
         */
        private String school;

        /**
         * 专业
         */
        private String major;

        /**
         * 学历
         */
        private String educationalQualifications;
    }

    @Data
    public static class WorkInfo {

        /**
         * 时间
         */
        private String timeArrange;

        /**
         * 公司名称
         */
        private String companyName;

        /**
         * 职位
         */
        private String position;

        /**
         * 所在部门
         */
        private String dept;

        /**
         * 工作职责
         */
        private String responsibilities;

        /**
         * 个人能力
         */
        private String personalAbility;
    }

    @Data
    public static class ProjectInfo {
        /**
         * 项目名称
         */
        private String projectName;
        /**
         * 项目职责
         */
        private String responsibilities;

        /**
         * 项目时间
         */
        private String timeArrange;

        /**
         * 技术栈
         */
        private String technologyStack;

        /**
         * 业绩
         */
        private String performance;

        /**
         * 项目描述
         */
        private String projectDescription;
    }


    /**
     * 推荐职位
     */
    private String recommendedPosition;

    /**
     * 顾问评价
     */
    private ConsultantEvaluation consultantEvaluation;

    /**
     * 基本信息/个人信息
     */
    private BaseInfo baseInfo;

    /**
     * 教育背景/教育经历
     */
    private List<EducationInfo> educationInfos;

    /**
     * 工作经历
     */
    private List<WorkInfo> workInfos;

    /**
     * 项目经历
     */
    private List<ProjectInfo> projectInfos;

    /**
     * 荣誉奖励
     */
    private String honorAward;

    /**
     * 专业技能
     */
    private String professionalSkill;


    public void printInfo() {
        String recommendedPositionInfo = String.format("\n\n推荐职位： " +
                "\n\t%s", recommendedPosition);
        String consultantEvaluationInfo = String.format(
                "\n\n顾问评价：" +
                        "\n\t离职原因:" +
                        "\n\t\t%s" +
                        "\n\t看机会原因:" +
                        "\n\t\t%s",
                consultantEvaluation.getDimissionReason(), consultantEvaluation.getChanceReason()
        );

        String baseInfos = String.format(
                "\n\n个人信息:" +
                        "\n\t姓名：" +
                        "\n\t\t%s" +
                        "\n\t年龄:" +
                        "\n\t\t%s" +
                        "\n\t性别:" +
                        "\n\t\t%s" +
                        "\n\t工作年限：" +
                        "\n\t\t%s" +
                        "\n\t当前状态：" +
                        "\n\t\t%s" +
                        "\n\t目前年薪：" +
                        "\n\t\t%s" +
                        "\n\t期望年薪：" +
                        "\n\t\t%s" +
                        "\n\t政治面貌：" +
                        "\n\t\t%s" +
                        "\n\t婚姻状况：" +
                        "\n\t\t%s" +
                        "\n\t目前所在地：" +
                        "\n\t\t%s" +
                        "\n\t期望所在地：" +
                        "\n\t\t%s" +
                        "\n\t到岗时间：" +
                        "\n\t\t%s" +
                        "\n\t求职动机：" +
                        "\n\t\t%s" +
                        "\n\t电话：" +
                        "\n\t\t%s" +
                        "\n\t邮箱：" +
                        "\n\t\t%s"
                ,
                baseInfo.getName(), baseInfo.getAge(), baseInfo.getSex(), baseInfo.getSeniority(), baseInfo.getWorkStatus(),
                baseInfo.getAnnualSalary(), baseInfo.getExpectedAnnualSalary(), baseInfo.getPoliticalStatus(), baseInfo.getMaritalStatus(),
                baseInfo.getPresentLocation(), baseInfo.getDesiredLocation(), baseInfo.getArrivalTime(), baseInfo.getJobSeekingMotivation(),
                baseInfo.getPhone(), baseInfo.getEmail()
        );


        // 教育背景信息格式化
        StringBuffer sbf = new StringBuffer();
        for (int i = 0; i < educationInfos.size(); i++) {
            EducationInfo educationInfo = educationInfos.get(i);
            String eduInfo = String.format("\n\n教育背景%d:" +
                            "\n\t时间：" +
                            "\n\t\t%s" +
                            "\n\t学校：" +
                            "\n\t\t%s" +
                            "\n\t专业：" +
                            "\n\t\t%s" +
                            "\n\t学历：" +
                            "\n\t\t%s"
                    , i+1, educationInfo.getTimeArrange(),
                    educationInfo.getSchool(), educationInfo.getMajor(), educationInfo.getEducationalQualifications());
            sbf.append(eduInfo);
        }
        String eduInfos = sbf.toString();



/*
        // 工作经历格式化
        sbf = new StringBuffer();
        for (int i = 0; i < workInfos.size(); i++) {
            WorkInfo wInfo = workInfos.get(i);
            String eduInfo = String.format("\n\n工作经历%d:" +
                            "\n\t时间：" +
                            "\n\t\t%s" +
                            "\n\t公司名称：" +
                            "\n\t\t%s" +
                            "\n\t职位：" +
                            "\n\t\t%s" +
                            "\n\t所在部门：" +
                            "\n\t\t%s" +
                            "\n\t工作职责：" +
                            "\n\t\t%s" +
                            "\n\t个人能力：" +
                            "\n\t\t%s"
                    , i+1, wInfo.getTimeArrange(),
                    wInfo.getCompanyName(), wInfo.getPosition(), wInfo.getDept(),
                    wInfo.getResponsibilities(), wInfo.getPersonalAbility());
            sbf.append(eduInfo);
        }
        String wInfos = sbf.toString();
*/


        // 项目经历格式化
        sbf = new StringBuffer();
        for (int i = 0; i < projectInfos.size(); i++) {
            ProjectInfo pInfo = projectInfos.get(i);
            String eduInfo = String.format("\n\n项目经历%d:" +
                            "\n\t项目名称：" +
                            "\n\t\t%s" +
                            "\n\t项目职责：" +
                            "\n\t\t%s" +
                            "\n\t项目时间：" +
                            "\n\t\t%s" +
                            "\n\t技术栈：" +
                            "\n\t\t%s" +
                            "\n\t业绩：" +
                            "\n\t\t%s" +
                            "\n\t项目描述：" +
                            "\n\t\t%s"
                    , i+1, pInfo.getProjectName(),
                    pInfo.getResponsibilities(), pInfo.getTimeArrange(), pInfo.getTechnologyStack(),
                    pInfo.getPerformance(), pInfo.getProjectDescription());
            sbf.append(eduInfo);
        }
        String pInfos = sbf.toString();

        String honorAwardInfo = String.format("\n\n荣誉奖励:" +
                "\n\t%s", honorAward);
        String professionalSkillInfo = String.format("\n\n专业技能:" +
                "\n\t%s", professionalSkill);

        System.out.println(pInfos);
    }
}

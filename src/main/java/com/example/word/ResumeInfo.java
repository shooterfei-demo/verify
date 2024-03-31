package com.example.word;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ResumeInfo {

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
}

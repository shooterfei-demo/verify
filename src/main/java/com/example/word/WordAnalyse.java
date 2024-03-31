package com.example.word;

import org.apache.poi.xwpf.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WordAnalyse {
    public static String analyseParaText(IBodyElement element) {
        String paraText = null;
        switch (element.getElementType()){
            case TABLE:
                XWPFTable table =(XWPFTable) element;
                paraText = table.getText();
                break;
            case PARAGRAPH:
                XWPFParagraph para = (XWPFParagraph)  element;
                paraText = para.getText();
                break;
            case CONTENTCONTROL:
                // 目录 -- 不做处理
//                            XWPFSDT sdt = (XWPFSDT) bodyElement;
//                            paraText = sdt.getContent().getText();
                break;
        }
        return paraText.trim();
    }

    public static String matchText(Pattern pattern, String text) {
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            return matcher.group(1).trim();
        }
        return null;
    }

    public static void main(String[] args) {
        File[] files = new File[5];
        files[0] = new File("/Users/wf/Documents/简历验证/简历1.docx");
        files[1] = new File("/Users/wf/Documents/简历验证/简历2.docx");
        files[2] = new File("/Users/wf/Documents/简历验证/简历3.docx");
        files[3] = new File("/Users/wf/Documents/简历验证/简历4.docx");
        files[4] = new File("/Users/wf/Documents/简历验证/简历5.docx");
        FileInputStream fis = null;
        XWPFDocument document = null;

        // 推荐职位筛选正则
        Pattern positionPattern = Pattern.compile("^推荐职位：(.*)$");
        // 顾问评价中 看机会原因与离职原因筛选正则
        Pattern chanceReasonPattern = Pattern.compile("^看机会原因：(.*)$");
        Pattern dimissionReasonPattern = Pattern.compile("^离职原因：(.*)$");
        // 基本/个人信息中正则
        Pattern namePattern = Pattern.compile("^姓[\\s]{0,}名[\\s]{0,}：\\s{0,}(\\S+)\\s.*$", Pattern.MULTILINE);
        Pattern agePattern = Pattern.compile("^.*年[\\s]{0,}龄[\\s]{0,}：\\s{0,}(\\S+)\\s.*$", Pattern.MULTILINE);
        Pattern sexPattern = Pattern.compile("^.*性[\\s]{0,}别[\\s]{0,}：\\s{0,}(\\S+)\\s.*$", Pattern.MULTILINE);
        Pattern seniorityPattern = Pattern.compile("^.*工作年限[\\s]{0,}：\\s{0,}(\\S+)\\s.*$", Pattern.MULTILINE);
        Pattern workStatusPattern = Pattern.compile("^.*当前状态[\\s]{0,}：\\s{0,}(\\S+)\\s.*$", Pattern.MULTILINE);
        Pattern annualSalaryPattern = Pattern.compile("^目前年薪[\\s]{0,}：\\s{0,}(.*)\\s{0,}期望年薪.*$", Pattern.MULTILINE);
        Pattern expectedAnnualSalaryPattern = Pattern.compile("^.*期望年薪[\\s]{0,}：\\s{0,}(\\S+).*$", Pattern.MULTILINE);
        Pattern politicalStatusPattern = Pattern.compile("^.*政治面貌[\\s]{0,}：\\s{0,}(\\S+)\\s.*$", Pattern.MULTILINE);
        Pattern maritalStatusPattern = Pattern.compile("^.*婚姻状况[\\s]{0,}：\\s{0,}(\\S+)\\s.*$", Pattern.MULTILINE);
        Pattern presentLocationPattern = Pattern.compile("^.*[目前所在地|所在城市][\\s]{0,}：\\s{0,}(\\S+)\\s.*$", Pattern.MULTILINE);
        Pattern desiredLocationPattern = Pattern.compile("^.*期望所在地[\\s]{0,}：\\s{0,}(\\S+)\\s.*$", Pattern.MULTILINE);
        Pattern arrivalTimePattern = Pattern.compile("^.*到岗时间[\\s]{0,}：\\s{0,}(\\S+)\\s.*$", Pattern.MULTILINE);
        Pattern jobSeekingMotivationPattern = Pattern.compile("^.*求职动机[\\s]{0,}：\\s{0,}(\\S+)\\s.*$", Pattern.MULTILINE);
        Pattern phonePattern = Pattern.compile("^.*电[\\s]{0,}话[\\s]{0,}：\\s{0,}(\\S+)\\s.*$", Pattern.MULTILINE);
        Pattern emailPattern = Pattern.compile("^.*邮[\\s]{0,}箱[\\s]{0,}：\\s{0,}(\\S+)\\s.*$", Pattern.MULTILINE);

        // 教育背景/教育经历


        try {
            for (int i = 0; i < files.length; i++) {
//            for (int i = 0; i < 1; i++) {
                fis = new FileInputStream(files[i]);
                document = new XWPFDocument(fis);
                List<IBodyElement> bodyElements = document.getBodyElements();
                ResumeInfo resumeInfo = new ResumeInfo();
                // 推荐职位相关下标索引
                int positionIndex = 0;
                // 顾问评价相关下标索引
                int consultantEvaluationStartIndex = 0, consultantEvaluationEndIndex = 0;
                // 基本/个人信息下标索引
                int baseInfoStartIndex = 0,  baseInfoEndIndex = 0;

                // 获取各内容模块范围
                for (int j = 0; j < bodyElements.size(); j++) {
                    IBodyElement bodyElement = bodyElements.get(j);
                    String paraText = analyseParaText(bodyElement);
                    if (paraText.contains("推荐职位")) {
                        positionIndex = j;
                    }
                    if (paraText.contains("顾问评价")) {
                        consultantEvaluationStartIndex = j;
                    }
                    if (paraText.contains("基本信息") || paraText.contains("个人信息")) {
                        consultantEvaluationEndIndex = j;
                        baseInfoStartIndex = j;
                    }
                    if (paraText.contains("教育背景") || paraText.contains("教育经历")) {
                        baseInfoEndIndex = j;
                    }
                }

                // 推荐职位相关内容处理
                {
                    IBodyElement bodyElement = bodyElements.get(positionIndex);
                    String paraText = analyseParaText(bodyElement);

                    String recommendedPosition = matchText(positionPattern, paraText);
                    if (recommendedPosition != null) {
                        resumeInfo.setRecommendedPosition(recommendedPosition);
                    }
                }

                // 顾问评价相关内容处理
                ResumeInfo.ConsultantEvaluation consultantEvaluation = new ResumeInfo.ConsultantEvaluation();
                for (int j = consultantEvaluationStartIndex + 1; j < consultantEvaluationEndIndex; j++) {
                    IBodyElement bodyElement = bodyElements.get(j);
                    String paraText = analyseParaText(bodyElement);
                    String chanceReason = matchText(chanceReasonPattern, paraText);
                    String dimissionReason = matchText(dimissionReasonPattern, paraText);
                    if (chanceReason != null) {
                        consultantEvaluation.setChanceReason(chanceReason);
                    }
                    if (dimissionReason != null) {
                        consultantEvaluation.setDimissionReason(dimissionReason);
                    }
                }
                resumeInfo.setConsultantEvaluation(consultantEvaluation);

                // 基本/个人信息相关内容处理
                ResumeInfo.BaseInfo baseInfo = new ResumeInfo.BaseInfo();
                for (int j = baseInfoStartIndex; j < baseInfoEndIndex; j++) {
                    IBodyElement bodyElement = bodyElements.get(j);
                    String paraText = analyseParaText(bodyElement);

                    System.out.println(paraText);

                    String name = matchText(namePattern, paraText);
                    String sex = matchText(sexPattern, paraText);
                    String age = matchText(agePattern, paraText);
                    String seniority = matchText(seniorityPattern, paraText);
                    String workStatus = matchText(workStatusPattern, paraText);
                    String annualSalary = matchText(annualSalaryPattern, paraText);
                    String expectedAnnualSalary = matchText(expectedAnnualSalaryPattern, paraText);
                    String politicalStatus = matchText(politicalStatusPattern, paraText);
                    String maritalStatus = matchText(maritalStatusPattern, paraText);
                    String presentLocation = matchText(presentLocationPattern, paraText);
                    String desiredLocation = matchText(desiredLocationPattern, paraText);
                    String arrivalTime = matchText(arrivalTimePattern, paraText);
                    String jobSeekingMotivation = matchText(jobSeekingMotivationPattern, paraText);
                    String phone = matchText(phonePattern, paraText);
                    String email = matchText(emailPattern, paraText);

                    if (name != null) {
                        baseInfo.setName(name);
                    }
                    if (age != null) {
                        baseInfo.setAge(age);
                    }
                    if (sex != null) {
                        baseInfo.setSex(sex);
                    }
                    if (seniority != null) {
                        baseInfo.setSeniority(seniority);
                    }
                    if (workStatus != null) {
                        baseInfo.setWorkStatus(workStatus);
                    }
                    if (annualSalary != null) {
                        baseInfo.setAnnualSalary(annualSalary);
                    }
                    if (expectedAnnualSalary != null) {
                        baseInfo.setExpectedAnnualSalary(expectedAnnualSalary);
                    }
                    if (politicalStatus != null) {
                        baseInfo.setPoliticalStatus(politicalStatus);
                    }
                    if (maritalStatus != null) {
                        baseInfo.setMaritalStatus(maritalStatus);
                    }
                    if (presentLocation != null) {
                        baseInfo.setPresentLocation(presentLocation);
                    }
                    if (desiredLocation != null) {
                        baseInfo.setDesiredLocation(desiredLocation);
                    }
                    if (arrivalTime != null) {
                        baseInfo.setArrivalTime(arrivalTime);
                    }
                    if (jobSeekingMotivation != null) {
                        baseInfo.setJobSeekingMotivation(jobSeekingMotivation);
                    }
                    if (phone != null) {
                        baseInfo.setPhone(phone);
                    }
                    if (email != null) {
                        baseInfo.setEmail(email);
                    }
//                    System.out.println(paraText);
                }
                resumeInfo.setBaseInfo(baseInfo);

                System.out.println(resumeInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

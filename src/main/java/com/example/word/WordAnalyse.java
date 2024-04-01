package com.example.word;

import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STOnOff;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WordAnalyse {
    public static String analyseParaText(IBodyElement element) {
        String paraText = null;
        switch (element.getElementType()) {
            case TABLE:
                XWPFTable table = (XWPFTable) element;
                paraText = table.getText();
                break;
            case PARAGRAPH:
                XWPFParagraph para = (XWPFParagraph) element;
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

    public static String matchText(Pattern pattern, String text, int index) {
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            return matcher.group(index).trim();
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
        Pattern annualSalaryPattern2 = Pattern.compile("^目前薪资状况[\\s]{0,}：(\\S+).*$", Pattern.MULTILINE);
        Pattern expectedAnnualSalaryPattern = Pattern.compile("^.*期望(年薪|薪资状况)[\\s]{0,}：\\s{0,}(\\S+).*$", Pattern.MULTILINE);
//        Pattern expectedAnnualSalaryPattern2 = Pattern.compile("^.*[\\s]{0,}：\\s{0,}(\\S+).*$", Pattern.MULTILINE);
        Pattern politicalStatusPattern = Pattern.compile("^.*政治面貌[\\s]{0,}：\\s{0,}(\\S+)\\s.*$", Pattern.MULTILINE);
        Pattern maritalStatusPattern = Pattern.compile("^.*婚姻状况[\\s]{0,}：\\s{0,}(\\S+)\\s.*$", Pattern.MULTILINE);
        Pattern presentLocationPattern = Pattern.compile("^.*[目前所在地|所在城市][\\s]{0,}：\\s{0,}(\\S+)\\s.*$", Pattern.MULTILINE);
        Pattern desiredLocationPattern = Pattern.compile("^.*期望所在地[\\s]{0,}：\\s{0,}(\\S+)\\s.*$", Pattern.MULTILINE);
        Pattern arrivalTimePattern = Pattern.compile("^.*到岗时间[\\s]{0,}：\\s{0,}(\\S+)\\s.*$", Pattern.MULTILINE);
        Pattern jobSeekingMotivationPattern = Pattern.compile("^.*求职动机[\\s]{0,}：\\s{0,}(\\S+)\\s.*$", Pattern.MULTILINE);
        Pattern phonePattern = Pattern.compile("^.*电[\\s]{0,}话[\\s]{0,}：\\s{0,}(\\S+)\\s.*$", Pattern.MULTILINE);
        Pattern emailPattern = Pattern.compile("^.*邮[\\s]{0,}箱[\\s]{0,}：\\s{0,}(\\S+).*$", Pattern.MULTILINE);

        // 教育背景/教育经历
        Pattern educationPattern = Pattern.compile("^(\\d{4}.\\d{2}[\\s]{0,}[–|-][\\s]{0,}\\d{4}.\\d{2})\\s+([\u4E00-\u9FA5]{1,}\\s{0,}(\\(.+\\)|（.+）){0,})\\s{1,}(\\S+)\\s{1,}(.*)$");


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
                int baseInfoStartIndex = 0, baseInfoEndIndex = 0;

                int educationInfosStartIndex = 0, educationInfosEndIndex = 0;

                // 荣誉奖励下标索引
                int honorAwardStartIndex = 0, honorAwardEndIndex = 0;
                // 专业技能下标索引
                int professionSkillStartIndex = 0, professionSkillEndIndex = 0;
                // 工作经历下标索引
                int workInfoStartIndex = 0, workInfoEndIndex = 0;
                // 项目经历下标索引
                int projectInfoStartIndex = 0, projectInfoEndIndex = 0;


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
                        educationInfosStartIndex = j;
                    }
                    if (paraText.contains("工作经历")) {
                        educationInfosEndIndex = j;
                    }
                    if (paraText.contains("项目经历")) {
                        projectInfoStartIndex = j;
                    }
                    if (paraText.contains("其他项目")) {
                        if (j > projectInfoStartIndex) {
                            projectInfoEndIndex = j;
                        }
                    }
                    if (paraText.contains("荣誉奖励")) {
                        honorAwardStartIndex = j;
                    }
                    if (honorAwardStartIndex > 0 && j > honorAwardStartIndex) {
                        XWPFParagraph para = (XWPFParagraph) bodyElement;
                        for (XWPFRun run : para.getRuns()) {
                            CTRPr fontProperties = run.getCTR().getRPr();
                            CTOnOff b = fontProperties.getB();
                            if (b != null && b.getVal() == STOnOff.ON) {
                                honorAwardEndIndex = j;
                            }
                        }

                    }

                    if (paraText.contains("专业技能")) {
                        professionSkillStartIndex = j;
                    }
                    if (professionSkillStartIndex > 0 && j > professionSkillStartIndex) {
                        XWPFParagraph para = (XWPFParagraph) bodyElement;
                        for (XWPFRun run : para.getRuns()) {
                            CTRPr fontProperties = run.getCTR().getRPr();
                            CTOnOff b = fontProperties.getB();
                            if (b != null && b.getVal() == STOnOff.ON) {
                                professionSkillEndIndex = j;
                            }
                        }
                    }
                }

                if (honorAwardStartIndex > 0 && honorAwardEndIndex == 0) {
                    honorAwardEndIndex = bodyElements.size() - 1;
                }

                if (professionSkillStartIndex > 0 && professionSkillEndIndex == 0) {
                    professionSkillEndIndex = bodyElements.size() - 1;
                }

                if (projectInfoStartIndex > 0 && projectInfoEndIndex == 0) {
                    projectInfoEndIndex = bodyElements.size() - 1;
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

//                    System.out.println(paraText);

                    String name = matchText(namePattern, paraText);
                    String sex = matchText(sexPattern, paraText);
                    String age = matchText(agePattern, paraText);
                    String seniority = matchText(seniorityPattern, paraText);
                    String workStatus = matchText(workStatusPattern, paraText);
                    String annualSalary = matchText(annualSalaryPattern, paraText);
                    String expectedAnnualSalary = matchText(expectedAnnualSalaryPattern, paraText, 2);
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
                    } else {
                        annualSalary = matchText(annualSalaryPattern2, paraText);
                        if (annualSalary != null) {
                            baseInfo.setAnnualSalary(annualSalary);
                        }
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

                // 教育背景/教育经历相关内容处理

                ArrayList<ResumeInfo.EducationInfo> educationInfos = new ArrayList<>();

                for (int j = educationInfosStartIndex + 1; j < educationInfosEndIndex; j++) {
                    IBodyElement bodyElement = bodyElements.get(j);
                    String paraText = analyseParaText(bodyElement);
                    Matcher matcher = educationPattern.matcher(paraText);
                    ResumeInfo.EducationInfo educationInfo = new ResumeInfo.EducationInfo();
                    while (matcher.find()) {
                        // 1 2 4 5
                        educationInfo.setTimeArrange(matcher.group(1));
                        educationInfo.setSchool(matcher.group(2));
                        educationInfo.setMajor(matcher.group(4));
                        educationInfo.setEducationalQualifications(matcher.group(5));
                        educationInfos.add(educationInfo);
                    }

//                    System.out.println(paraText);
                }
                resumeInfo.setEducationInfos(educationInfos);


                // 项目经历相关内容处理
                ArrayList<ResumeInfo.ProjectInfo> projectInfos = new ArrayList<>();

                int template = 0;
                if (projectInfoStartIndex > 0) {
                    for (int j = projectInfoStartIndex + 1; j < projectInfoEndIndex; j++) {
                        IBodyElement bodyElement = bodyElements.get(j);
                        XWPFParagraph para = (XWPFParagraph) bodyElement;
                        String paraText = analyseParaText(bodyElement);
                        if ("描述：".equals(paraText)) {
                            template = 1;
                            break;
                        }
                        if (paraText.startsWith("技术栈：")) {
                            template = 4;
                            break;
                        }
                        if (paraText.startsWith("项目职责：")) {
                            template = 5;
                            break;
                        }
                    }
                    System.out.println(template);
                    if (template == 1) {
//                        Pattern projectNamePattern1 = Pattern.compile("^(.+)$");
                        Pattern projectNamePattern1 = Pattern.compile("^(\\d{4}.\\d{2}-\\d{4}.\\d{2}\\s+){0,}(\\S+)$");
                        ResumeInfo.ProjectInfo projectInfo = null;
                        StringBuffer responsibilitiesSbf = new StringBuffer();
                        boolean responsibilitiesFlag = false;
                        for (int j = projectInfoStartIndex + 1; j < projectInfoEndIndex; j++) {
                            IBodyElement bodyElement = bodyElements.get(j);
                            XWPFParagraph para = (XWPFParagraph) bodyElement;
                            String paraText = analyseParaText(bodyElement);
                            boolean flag = false;
                            for (XWPFRun run : para.getRuns()) {
                                CTRPr fontProperties = run.getCTR().getRPr();
                                CTOnOff b = fontProperties.getB();
                                if (b != null) {
                                    if (!"描述：".equals(paraText)) {
                                        if (projectInfo != null) {
                                            projectInfo.setResponsibilities(responsibilitiesSbf.toString());
                                            responsibilitiesSbf = new StringBuffer();
                                            responsibilitiesFlag = false;
                                        }
                                        projectInfo = new ResumeInfo.ProjectInfo();
                                        projectInfos.add(projectInfo);
                                        flag = true;
                                        break;
                                    }
                                }
                            }
                            if (flag) {
                                Matcher matcher = projectNamePattern1.matcher(paraText);
                                while (matcher.find()) {
                                    projectInfo.setTimeArrange(matcher.group(1));
                                    projectInfo.setProjectName(matcher.group(2));
                                }
                            } else {
                                if (responsibilitiesFlag) {
                                    responsibilitiesSbf.append(paraText);
                                }
                            }

                            if (paraText.contains("主要职责")) {
                                responsibilitiesFlag = true;
                            }
/*                        for (XWPFRun run : para.getRuns()) {
                            CTRPr fontProperties = run.getCTR().getRPr();
                            CTOnOff b = fontProperties.getB();
                            if (b != null && b.getVal() == STOnOff.ON) {
                                System.out.println(123412);
                                if (!paraText.contains("描述")) {
                                    System.out.println(1234);
                                    // 项目名index
                                    Matcher matcher = projectNamePattern1.matcher(paraText);
                                    while (matcher.find()) {
                                        System.out.println(matcher.group(2));
                                    }
//                                    projectInfo.setProjectName()

                                }
                            }
                        }*/
                        }
                        if (projectInfo != null) {
                            projectInfo.setResponsibilities(responsibilitiesSbf.toString());
                            responsibilitiesFlag = false;
                        }
                    }
                    if (template == 5) {
                        Pattern projectNamePattern5 = Pattern.compile("^(.+)$", Pattern.MULTILINE);
                        Pattern responsibilitiesPattern5 = Pattern.compile("^项目职责：(.+)$", Pattern.MULTILINE);
                        ResumeInfo.ProjectInfo projectInfo = null;
                        StringBuffer responsibilitiesSbf = new StringBuffer();
                        for (int j = projectInfoStartIndex + 1; j <= projectInfoEndIndex; j++) {
                            IBodyElement bodyElement = bodyElements.get(j);
                            XWPFParagraph para = (XWPFParagraph) bodyElement;
                            String paraText = analyseParaText(bodyElement);
//                            System.out.println(paraText);
//                            System.out.println("------");
                            boolean flag = false;
                            for (XWPFRun run : para.getRuns()) {
                                CTRPr fontProperties = run.getCTR().getRPr();
                                CTOnOff b = fontProperties.getB();
                                if (b != null) {
                                    flag = true;
                                    break;
                                }
                            }

                            if (flag) {
//                                System.out.println(paraText);
                                projectInfo = new ResumeInfo.ProjectInfo();
                                Matcher matcher = projectNamePattern5.matcher(paraText);
                                while (matcher.find()) {
                                    projectInfo.setProjectName(matcher.group(1));
                                }
                            } else {
                                Matcher matcher = responsibilitiesPattern5.matcher(paraText);
                                while (matcher.find()) {
                                    projectInfo.setResponsibilities(matcher.group(1));
                                    projectInfos.add(projectInfo);
                                }
                            }

/*                        for (XWPFRun run : para.getRuns()) {
                            CTRPr fontProperties = run.getCTR().getRPr();
                            CTOnOff b = fontProperties.getB();
                            if (b != null && b.getVal() == STOnOff.ON) {
                                System.out.println(123412);
                                if (!paraText.contains("描述")) {
                                    System.out.println(1234);
                                    // 项目名index
                                    Matcher matcher = projectNamePattern1.matcher(paraText);
                                    while (matcher.find()) {
                                        System.out.println(matcher.group(2));
                                    }
//                                    projectInfo.setProjectName()

                                }
                            }
                        }*/
                        }
                    }
                }
                resumeInfo.setProjectInfos(projectInfos);

                // 荣誉奖励内容处理
                if (honorAwardStartIndex > 0) {
                    StringBuilder sbd = new StringBuilder();
                    for (int j = honorAwardStartIndex + 1; j <= honorAwardEndIndex; j++) {
                        IBodyElement bodyElement = bodyElements.get(j);
                        sbd.append(analyseParaText(bodyElement));
                    }
                    resumeInfo.setHonorAward(sbd.toString());
                }

                // 专业技能内容处理
                if (professionSkillStartIndex > 0) {
                    StringBuilder sbd = new StringBuilder();
                    for (int j = professionSkillStartIndex + 1; j <= professionSkillEndIndex; j++) {
                        IBodyElement bodyElement = bodyElements.get(j);
                        sbd.append(analyseParaText(bodyElement));
                    }
                    resumeInfo.setProfessionalSkill(sbd.toString());
                }

                resumeInfo.printInfo();
//                System.out.println(resumeInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

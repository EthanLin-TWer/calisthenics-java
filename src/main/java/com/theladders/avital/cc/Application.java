package com.theladders.avital.cc;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Map.*;

public class Application {
    private final HashMap<String, List<List<String>>> jobs = new HashMap<>();
    private final HashMap<String, List<List<String>>> applied = new HashMap<>();
    private final List<List<String>> failedApplications = new ArrayList<>();

    public void execute(String command, String employerName, String jobName, String jobType, String jobSeekerName, String resumeApplicantName, LocalDate applicationTime) throws NotSupportedJobTypeException, RequiresResumeForJReqJobException, InvalidResumeException {
        if (command == "publish") {
            if (!jobType.equals("JReq") && !jobType.equals("ATS")) {
                throw new NotSupportedJobTypeException();
            }

            List<List<String>> alreadyPublished = jobs.getOrDefault(employerName, new ArrayList<>());

            alreadyPublished.add(new ArrayList<String>() {{
                add(jobName);
                add(jobType);
            }});
            jobs.put(employerName, alreadyPublished);
        } else if (command == "save") {
            List<List<String>> saved = jobs.getOrDefault(employerName, new ArrayList<>());

            saved.add(new ArrayList<String>() {{
                add(jobName);
                add(jobType);
            }});
            jobs.put(employerName, saved);
        } else if (command == "apply") {
            if (jobType.equals("JReq") && resumeApplicantName == null) {
                List<String> failedApplication = new ArrayList<String>() {{
                    add(jobName);
                    add(jobType);
                    add(applicationTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                    add(employerName);
                }};
                failedApplications.add(failedApplication);
                throw new RequiresResumeForJReqJobException();
            }

            if (jobType.equals("JReq") && !resumeApplicantName.equals(jobSeekerName)) {
                throw new InvalidResumeException();
            }
            List<List<String>> saved = this.applied.getOrDefault(jobSeekerName, new ArrayList<>());

            saved.add(new ArrayList<String>() {{
                add(jobName);
                add(jobType);
                add(applicationTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                add(employerName);
            }});
            applied.put(jobSeekerName, saved);
        }
    }

    public List<List<String>> getJobs(String employerName, String type) {
        if (type.equals("applied")) {
            return applied.get(employerName);
        }

        return jobs.get(employerName);
    }

    public List<String> findApplicants(String jobName, String employerName) {
        return findApplicants(jobName, employerName, null);
    }

    public List<String> findApplicants(String jobName, String employerName, LocalDate from) {
        return findApplicants(jobName, employerName, from, null);
    }

    public List<String> findApplicants(String jobName, String employerName, LocalDate from, LocalDate to) {
        if (from == null && to == null) {
            List<String> result = new ArrayList<String>() {};
            Iterator<Entry<String, List<List<String>>>> iterator = this.applied.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry<String, List<List<String>>> set = iterator.next();
                String applicant = set.getKey();
                List<List<String>> jobs = set.getValue();
                boolean hasAppliedToThisJob = jobs.stream().anyMatch(job -> job.get(0).equals(jobName));
                if (hasAppliedToThisJob) {
                    result.add(applicant);
                }
            }
            return result;
        } else if (jobName == null && to == null) {
            List<String> result = new ArrayList<String>() {};
            Iterator<Entry<String, List<List<String>>>> iterator = this.applied.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry<String, List<List<String>>> set = iterator.next();
                String applicant = set.getKey();
                List<List<String>> jobs = set.getValue();
                boolean isAppliedThisDate = jobs.stream().anyMatch(job ->
                        !from.isAfter(LocalDate.parse(job.get(2), DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
                if (isAppliedThisDate) {
                    result.add(applicant);
                }
            }
            return result;
        } else if (jobName == null && from == null) {
            List<String> result = new ArrayList<String>() {};
            Iterator<Entry<String, List<List<String>>>> iterator = this.applied.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry<String, List<List<String>>> set = iterator.next();
                String applicant = set.getKey();
                List<List<String>> jobs = set.getValue();
                boolean isAppliedThisDate = jobs.stream().anyMatch(job ->
                        !to.isBefore(LocalDate.parse(job.get(2), DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
                if (isAppliedThisDate) {
                    result.add(applicant);
                }
            }
            return result;

        } else if (jobName == null) {
            List<String> result = new ArrayList<String>() {};
            Iterator<Entry<String, List<List<String>>>> iterator = this.applied.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry<String, List<List<String>>> set = iterator.next();
                String applicant = set.getKey();
                List<List<String>> jobs = set.getValue();
                boolean isAppliedThisDate = jobs.stream().anyMatch(job -> !from.isAfter(LocalDate.parse(job.get(2), DateTimeFormatter.ofPattern("yyyy-MM-dd"))) && !to.isBefore(LocalDate.parse(job.get(2), DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
                if (isAppliedThisDate) {
                    result.add(applicant);
                }
            }
            return result;

        } else if (to != null) {
            List<String> result = new ArrayList<String>() {};
            Iterator<Entry<String, List<List<String>>>> iterator = this.applied.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry<String, List<List<String>>> set = iterator.next();
                String applicant = set.getKey();
                List<List<String>> jobs = set.getValue();
                boolean isAppliedThisDate = jobs.stream().anyMatch(job -> job.get(0).equals(jobName) && !to.isBefore(LocalDate.parse(job.get(2), DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
                if (isAppliedThisDate) {
                    result.add(applicant);
                }
            }
            return result;
        } else {
            List<String> result = new ArrayList<String>() {};
            Iterator<Entry<String, List<List<String>>>> iterator = this.applied.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry<String, List<List<String>>> set = iterator.next();
                String applicant = set.getKey();
                List<List<String>> jobs = set.getValue();
                boolean isAppliedThisDate = jobs.stream().anyMatch(job -> job.get(0).equals(jobName) && !from.isAfter(LocalDate.parse(job.get(2), DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
                if (isAppliedThisDate) {
                    result.add(applicant);
                }
            }
            return result;
        }
    }

    public String export(String type, LocalDate date) {
        if (type == "csv") {
            String result = "Employer,Job,Job Type,Applicants,Date" + "\n";
            Iterator<Entry<String, List<List<String>>>> iterator = this.applied.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry<String, List<List<String>>> set = iterator.next();
                String applicant = set.getKey();
                List<List<String>> jobs1 = set.getValue();
                List<List<String>> appliedOnDate = jobs1.stream().filter(job -> job.get(2).equals(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))).collect(Collectors.toList());

                for (List<String> job : appliedOnDate) {
                    result = result.concat(job.get(3) + "," + job.get(0) + "," + job.get(1) + "," + applicant + "," + job.get(2) + "\n");
                }
            }
            return result;
        } else {
            String content = "";
            Iterator<Entry<String, List<List<String>>>> iterator = this.applied.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry<String, List<List<String>>> set = iterator.next();
                String applicant = set.getKey();
                List<List<String>> jobs1 = set.getValue();
                List<List<String>> appliedOnDate = jobs1.stream().filter(job -> job.get(2).equals(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))).collect(Collectors.toList());

                for (List<String> job : appliedOnDate) {
                    content = content.concat("<tr>" + "<td>" + job.get(3) + "</td>" + "<td>" + job.get(0) + "</td>" + "<td>" + job.get(1) + "</td>" + "<td>" + applicant + "</td>" + "<td>" + job.get(2) + "</td>" + "</tr>");
                }
            }

            return "<!DOCTYPE html>"
                    + "<body>"
                    + "<table>"
                    + "<thead>"
                    + "<tr>"
                    + "<th>Employer</th>"
                    + "<th>Job</th>"
                    + "<th>Job Type</th>"
                    + "<th>Applicants</th>"
                    + "<th>Date</th>"
                    + "</tr>"
                    + "</thead>"
                    + "<tbody>"
                    + content
                    + "</tbody>"
                    + "</table>"
                    + "</body>"
                    + "</html>";
        }
    }

    public int getSuccessfulApplications(String employerName, String jobName) {
        int result = 0;
        Iterator<Entry<String, List<List<String>>>> iterator = this.applied.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<String, List<List<String>>> set = iterator.next();
            List<List<String>> jobs = set.getValue();

            result += jobs.stream().anyMatch(job -> job.get(3).equals(employerName) && job.get(0).equals(jobName)) ? 1 : 0;
        }
        return result;
    }

    public int getUnsuccessfulApplications(String employerName, String jobName) {
        return (int) failedApplications.stream().filter(job -> job.get(0).equals(jobName) && job.get(3).equals(employerName)).count();
    }
}

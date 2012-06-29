package org.motechproject.ananya.domain;

public enum ServiceType {
    JOB_AID {
        @Override
        public boolean isJobAid() {
            return true;
        }

        @Override
        public boolean isCertificateCourse() {
            return false;
        }

    },
    CERTIFICATE_COURSE {
        @Override
        public boolean isJobAid() {
            return false;
        }

        @Override
        public boolean isCertificateCourse() {
            return true;
        }
    };

    public abstract boolean isJobAid();

    public abstract boolean isCertificateCourse();


}

package org.motechproject.bbcwt.ivr;

public class IVR {
    public enum CallState {
        COLLECT_PIN, AUTH_SUCCESS;

        public boolean isCollectPin() {
            return this.equals(COLLECT_PIN);
        }
    }

    public enum Event {
        NEW_CALL("NewCall"), RECORD("Record"), GOT_DTMF("GotDTMF"), HANGUP("Hangup"), DISCONNECT("Disconnect");

        private String key;

        Event(String key) {
            this.key = key;
        }

        public String key() {
            return key;
        }

        public static Event keyOf(String key) {
            for (Event event : values())
                if (event.is(key)) return event;
            return null;
        }

        private boolean is(String key) {
            return this.key.equalsIgnoreCase(key);
        }
    }

    public static class Attributes {
        public static final String CALL_STATE = "call_state";
        public static final String CALL_ID = "call_id";
        public static final String CALLER_ID = "caller_id";
        public static final String PATIENT_DOCUMENT_ID = "patient_doc_id";
        public static final String NUMBER_OF_ATTEMPTS = "number_of_retries";
    }
}

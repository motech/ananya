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
        public static final String CALLER_ID = "caller_id";
        public static final String NAVIGATION_POST_HELP = "navigation_post_help";
        public static final String NEXT_INTERACTION = "next_interaction";
        public static final String DTMF_BUILDER = "dtmf_builder";
        public static final String RESPONSE_BUILDER = "ivr_response_builder";
        public static final String INVALID_INPUT_COUNT = "invalid_input_count";
        public static final String NO_INPUT_COUNT = "no_input_count";
        public static final String CURRENT_IVR_ACTION = "current_ivr_action";
        public static final String FLOW_SPECIFIC_STATE = "flow_specific_state";
        public static final String FIRST_INTERACTION_IN_CALL = "first_interaction_in_call";
    }
}

package com.example.marcello.providers.Requirements;

import java.util.ArrayList;

public class CalendarRequirements {

    public static class InsertCalendar{
        private static final String MESSAGE_TITLE = "Event Title";
        private static final String MESSAGE_DESCRIPTION = "Event Description";
        private static final String MESSAGE_BEGIN_TIME = "Begin Date";
        private static final String MESSAGE_END_TIME = "End Date";
        private static final String MESSAGE_TIME_ZONE = "Event Time Zone";

        private static final String ENTITY_TITLE = "title";
        private static final String ENTITY_DESCRIPTION = "description";
        private static final String ENTITY_BEGIN_TIME = "startDate";
        private static final String ENTITY_END_TIME = "endDate";
        private static final String ENTITY_TIME_ZONE = "eventTimeZone";

        public static final ArrayList<String> MESSAGES = new ArrayList<String>() {
            {
                add(MESSAGE_TITLE);
                add(MESSAGE_DESCRIPTION);
                add(MESSAGE_BEGIN_TIME);
                add(MESSAGE_END_TIME);
                add(MESSAGE_TIME_ZONE);
            }
        };
        public static final ArrayList<String> REQUIREMENTS = new ArrayList<String>() {
            {
                add(ENTITY_BEGIN_TIME);
                add(ENTITY_END_TIME);
                add(ENTITY_TITLE);
                add(ENTITY_DESCRIPTION);
                add(ENTITY_TIME_ZONE);
            }
        };
    }
}

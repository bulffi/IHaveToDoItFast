package database;

public class NoteSchema {
    public static final class NoteTable {
        public static final String NAME = "notes";
        public static final class Cols{
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String SOLVED = "solved";
            public static final String RELATED_PERSON = "related";
            public static final String DETAIL = "detail";
        }
    }
}

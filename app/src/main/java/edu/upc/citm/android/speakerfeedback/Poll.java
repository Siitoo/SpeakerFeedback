package edu.upc.citm.android.speakerfeedback;

import java.util.Date;
import java.util.List;

public class Poll {
    private String hash_question;
    private String question;
    private List<String> options;
    private boolean open;
    private Date start, end;
    private List<Integer> results;

    Poll(){}

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<String> getOptions() {
        return options;
    }

    public String getOptionsAsString()
    {
        //Així se sument molts strings a Java
        StringBuilder b = new StringBuilder();

        for(String op : options)
        {
            b.append(op);
            b.append("\n");
        }
        return b.toString();
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public List<Integer> getResults() {
        return results;
    }

    public void setResults(List<Integer> results) {
        this.results = results;
    }

    public String getHash_question() {
        return hash_question;
    }

    public void setHash_question(String hash_question) {
        this.hash_question = hash_question;
    }
}

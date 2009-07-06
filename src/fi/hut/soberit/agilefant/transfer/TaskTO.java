package fi.hut.soberit.agilefant.transfer;

import java.util.ArrayList;
import java.util.Collection;

import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.util.ResponsibleContainer;
import flexjson.JSON;

public class TaskTO extends Task {
   
    // Helper fields
    
    private Collection<ResponsibleContainer> userData = new ArrayList<ResponsibleContainer>();
    private Collection<HourEntryTO> hourEntries = new ArrayList<HourEntryTO>();
    private long effortSpent;    

    public TaskTO(Task task) {
        this.setId(task.getId());
        this.setName(task.getName());
        this.setDescription(task.getDescription());
        this.setIteration(task.getIteration());
        this.setStory(task.getStory());
        this.setState(task.getState());
        this.setPriority(task.getPriority());
        this.setEffortLeft(task.getEffortLeft());
        this.setOriginalEstimate(task.getOriginalEstimate());
        this.setResponsibles(task.getResponsibles());
        this.setCreatedDate(task.getCreatedDate());
        this.setCreator(task.getCreator());
    }
    
    public TaskTO(Task task, Collection<HourEntryTO> hourEntries) {
        this(task);
        this.setHourEntries(hourEntries);
    }
    
    public void setUserData(Collection<ResponsibleContainer> userData) {
        this.userData = userData;
    }

    @JSON(include = true)
    public Collection<ResponsibleContainer> getUserData() {
        return userData;
    }

    public void setHourEntries(Collection<HourEntryTO> hourEntries) {
        this.hourEntries = hourEntries;
    }

    @JSON(include = true)
    public Collection<HourEntryTO> getHourEntries() {
        return hourEntries;
    }

    public long getEffortSpent() {
        return effortSpent;
    }

    public void setEffortSpent(long effortSpent) {
        this.effortSpent = effortSpent;
    }

}

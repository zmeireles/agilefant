package fi.hut.soberit.agilefant.web;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.opensymphony.xwork2.Action;

import fi.hut.soberit.agilefant.annotations.PrefetchId;
import fi.hut.soberit.agilefant.business.ProjectBusiness;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.transfer.ProjectMetrics;

@Component("projectAction")
@Scope("prototype")
public class ProjectAction implements CRUDAction, Prefetching {
    
    private static final long serialVersionUID = -4636900464606739866L;

    @PrefetchId
    private int projectId;
    
    private Integer productId = null;

    private Integer rankUnderId = null;
    
    private Project project;
    
    private ProjectMetrics projectMetrics;
    
    private List<Story> stories;

    private Set<Integer> assigneeIds = new HashSet<Integer>();
    
    private boolean assigneesChanged = false;
    
    @Autowired
    private ProjectBusiness projectBusiness;
    
    public String rankProject() {
        project = projectBusiness.rankUnderProject(projectId, rankUnderId);
        return Action.SUCCESS;
    }
    
    public String unrankProject() {
        projectBusiness.unrankProject(projectId);
        return Action.SUCCESS;
    }
    
    public String projectMetrics() {
        project = projectBusiness.retrieve(projectId);
        projectMetrics = projectBusiness.getProjectMetrics(project);
        return Action.SUCCESS;
    }
    
    public String projectData() {
        project = projectBusiness.getProjectData(projectId);
        return Action.SUCCESS;
    }
    
    public String create() {
        project = new Project();
        project.setStartDate(new DateTime());
        project.setEndDate(new DateTime());
        return Action.SUCCESS;
    }

    public String retrieve() {
        project = this.projectBusiness.retrieve(projectId);
        return Action.SUCCESS;
    }
    
    public String retrieveRootStories() {
        project = this.projectBusiness.retrieve(projectId);
        stories = this.projectBusiness.retrievetRootStories(project);
        return Action.SUCCESS;
    }
    
    public String store() {
       Set<Integer> assignees = null;
       if (assigneesChanged) {
           assignees = assigneeIds;
       }
       project = this.projectBusiness.store(projectId, productId, project, assignees);
       return Action.SUCCESS;
    }
    
    public String delete() {
        this.projectBusiness.delete(projectId);
        return Action.SUCCESS;
    }

    public void initializePrefetchedData(int objectId) {
        this.project = this.projectBusiness.retrieve(objectId);        
    }
    
    //GETTERS AND SETTERS
    
    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
	
    public void setProjectBusiness(ProjectBusiness projectBusiness) {
        this.projectBusiness = projectBusiness;
    }

    public ProjectMetrics getProjectMetrics() {
        return projectMetrics;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public List<Story> getStories() {
        return stories;
    }
    
    public Integer getRankUnderId() {
        return rankUnderId;
    }
    public void setRankUnderId(Integer rankUnderId) {
        this.rankUnderId = rankUnderId;
    }
    public Set<Integer> getAssigneeIds() {
        return assigneeIds;
    }
    
    public void setAssigneeIds(Set<Integer> assigneeIds) {
        this.assigneeIds = assigneeIds;
    }
    
    public void setAssigneesChanged(boolean assigneesChanged) {
        this.assigneesChanged = assigneesChanged;
    }
    
    public boolean isAssigneesChanged() {
        return assigneesChanged;
    }
    
}

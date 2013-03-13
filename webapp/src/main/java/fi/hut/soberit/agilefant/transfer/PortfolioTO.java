package fi.hut.soberit.agilefant.transfer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.Team;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.security.SecurityUtil;

public class PortfolioTO {

    private List<Project> rankedProjects = Collections.emptyList();
    private Collection<Project> unrankedProjects = Collections.emptySet();
    private Integer timeSpanInDays = 0;

    public List<Project> getRankedProjects() {
        User loggedUser = getLoggedInUser();
        if (loggedUser.isAdmin()) {
            return rankedProjects;
        }
        List<Project> myRankedProjects = new ArrayList<Project>();
        for (Project project: rankedProjects) {
            if (project.getParent().isProduct()) {
                Product product = (Product) project.getParent();
                for (Team myTeam: loggedUser.getTeams()) {
                    if (product.getTeams().contains(myTeam)) {
                        myRankedProjects.add(project);
                        break;
                    }
                }
            }
        }
        return myRankedProjects;
    }

    public void setRankedProjects(List<Project> rankedProjects) {
        this.rankedProjects = rankedProjects;
    }

    public Collection<Project> getUnrankedProjects() {
        User loggedUser = getLoggedInUser();
        if (loggedUser.isAdmin()) {
            return unrankedProjects;
        }
        List<Project> myUnrankedProjects = new ArrayList<Project>();
        for (Project project: unrankedProjects) {
            if (project.getParent().isProduct()) {
                Product product = (Product) project.getParent();
                for (Team myTeam: loggedUser.getTeams()) {
                    if (product.getTeams().contains(myTeam)) {
                        myUnrankedProjects.add(project);
                        break;
                    }
                }
            }
        }
        return myUnrankedProjects;
    }
    private User getLoggedInUser() {
        return SecurityUtil.getLoggedUser();
    }

    public void setUnrankedProjects(Collection<Project> unrankedProjects) {
        this.unrankedProjects = unrankedProjects;
    }

    public void setTimeSpanInDays(Integer timeSpanInDays) {
        this.timeSpanInDays = timeSpanInDays;
    }

    public Integer getTimeSpanInDays() {
        return timeSpanInDays;
    }

}

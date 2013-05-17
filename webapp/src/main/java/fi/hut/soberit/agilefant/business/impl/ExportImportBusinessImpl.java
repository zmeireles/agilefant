package fi.hut.soberit.agilefant.business.impl;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.ExportImportBusiness;
import fi.hut.soberit.agilefant.db.AssignmentDAO;
import fi.hut.soberit.agilefant.db.BacklogHistoryEntryDAO;
import fi.hut.soberit.agilefant.db.BacklogHourEntryDAO;
import fi.hut.soberit.agilefant.db.HolidayDAO;
import fi.hut.soberit.agilefant.db.HourEntryDAO;
import fi.hut.soberit.agilefant.db.IterationDAO;
import fi.hut.soberit.agilefant.db.IterationHistoryEntryDAO;
import fi.hut.soberit.agilefant.db.LabelDAO;
import fi.hut.soberit.agilefant.db.ProductDAO;
import fi.hut.soberit.agilefant.db.ProjectDAO;
import fi.hut.soberit.agilefant.db.SettingDAO;
import fi.hut.soberit.agilefant.db.StoryAccessDAO;
import fi.hut.soberit.agilefant.db.StoryDAO;
import fi.hut.soberit.agilefant.db.StoryHourEntryDAO;
import fi.hut.soberit.agilefant.db.StoryRankDAO;
import fi.hut.soberit.agilefant.db.TaskDAO;
import fi.hut.soberit.agilefant.db.TaskHourEntryDAO;
import fi.hut.soberit.agilefant.db.TeamDAO;
import fi.hut.soberit.agilefant.db.UserDAO;
import fi.hut.soberit.agilefant.db.WhatsNextEntryDAO;
import fi.hut.soberit.agilefant.db.WhatsNextStoryEntryDAO;
import fi.hut.soberit.agilefant.db.WidgetCollectionDAO;
import fi.hut.soberit.agilefant.model.Assignment;
import fi.hut.soberit.agilefant.model.BacklogHistoryEntry;
import fi.hut.soberit.agilefant.model.BacklogHourEntry;
import fi.hut.soberit.agilefant.model.Holiday;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.IterationHistoryEntry;
import fi.hut.soberit.agilefant.model.Label;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.Setting;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.StoryAccess;
import fi.hut.soberit.agilefant.model.StoryHourEntry;
import fi.hut.soberit.agilefant.model.StoryRank;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.TaskHourEntry;
import fi.hut.soberit.agilefant.model.Team;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.model.WhatsNextEntry;
import fi.hut.soberit.agilefant.model.WhatsNextStoryEntry;
import fi.hut.soberit.agilefant.model.WidgetCollection;

@Service("exportBusiness")
public class ExportImportBusinessImpl implements ExportImportBusiness {

	@Autowired AssignmentDAO assignmentDAO;
	@Autowired BacklogHistoryEntryDAO backlogHistoryEntryDAO;
	@Autowired BacklogHourEntryDAO backlogHourEntryDAO;
	@Autowired HolidayDAO holidayDAO;
	@Autowired HourEntryDAO hourEntryDAO;
	@Autowired IterationDAO iterationDAO;
	@Autowired IterationHistoryEntryDAO iterationHistoryEntryDAO;
	@Autowired LabelDAO labelDAO;
	@Autowired ProductDAO productDAO;
	@Autowired ProjectDAO projectDAO;
	@Autowired SettingDAO settingDAO;
	@Autowired StoryDAO storyDAO;
	@Autowired StoryAccessDAO storyAccessDAO;
	@Autowired StoryHourEntryDAO storyHourEntryDAO;
	@Autowired StoryRankDAO storyRankDAO;
	@Autowired TaskDAO taskDAO;
	@Autowired TaskHourEntryDAO taskHourEntryDAO;
	@Autowired TeamDAO teamDAO;
	@Autowired UserDAO userDAO;
	@Autowired WhatsNextEntryDAO whatsNextEntryDAO;
	@Autowired WhatsNextStoryEntryDAO whatsNextStoryEntryDAO;
	@Autowired WidgetCollectionDAO widgetCollectionDAO;
	
	@Autowired
	SessionFactory sessionFactory;
	
	private void addInOrder(Story story, Collection<Story> stories) {
		Story parent = story.getParent();
		if(parent!=null && !stories.contains(parent)) {
			this.addInOrder(parent, stories);
		}
		stories.add(story);
	}
	
	@Override
	@Transactional(readOnly=true)
	public OrganizationDumpTO exportOrganization() {

		OrganizationDumpTO organizationTO = new OrganizationDumpTO();
		
		organizationTO.assignments = this.assignmentDAO.getAll();
		organizationTO.backlogHistoryEntries = this.backlogHistoryEntryDAO.getAll();
		organizationTO.backlogHourEntries = this.backlogHourEntryDAO.getAll();
		organizationTO.holidays = this.holidayDAO.getAll();
		organizationTO.iterations = this.iterationDAO.getAll();
		organizationTO.iterationHistoryEntries = this.iterationHistoryEntryDAO.getAll();
		organizationTO.labels = this.labelDAO.getAll();
		organizationTO.products = this.productDAO.getAll();
		organizationTO.projects = this.projectDAO.getAll();
		organizationTO.settings = this.settingDAO.getAll();
		
		organizationTO.stories = new LinkedHashSet<Story>();
		for(Story story : this.storyDAO.getAll()) {
			this.addInOrder(story, organizationTO.stories);
		}
		
		organizationTO.storyAccesses = this.storyAccessDAO.getAll();
		organizationTO.storyHourEntries = this.storyHourEntryDAO.getAll();
		organizationTO.storyRanks = this.storyRankDAO.getAll();
		organizationTO.tasks = this.taskDAO.getAll();
		organizationTO.taskHourEntries = this.taskHourEntryDAO.getAll();
		organizationTO.teams = this.teamDAO.getAll();
		organizationTO.users = this.userDAO.getAll();
		organizationTO.whatsNextEntries = this.whatsNextEntryDAO.getAll();
		organizationTO.whatsNextStoryEntries = this.whatsNextStoryEntryDAO.getAll();
		organizationTO.widgetCollections = this.widgetCollectionDAO.getAll();				
		
		return organizationTO;
	}
	
	@Override
	@Transactional
	public void importOrganization(OrganizationDumpTO organizationTO) {
		for(User user : organizationTO.users) {
			user.setLoginName(user.getLoginName() + new Date().getTime());
		}
		
		for(User user : organizationTO.users) {
			this.userDAO.store(user);
		}
		for(Holiday holiday : organizationTO.holidays) {
			this.holidayDAO.store(holiday);
		}
		for(Product product : organizationTO.products) {
			this.productDAO.store(product);
		}
		for(Project project : organizationTO.projects) {
			this.projectDAO.store(project);
		}
		for(Iteration iteration : organizationTO.iterations) {
			this.iterationDAO.store(iteration);
		}
		for(Story story : organizationTO.stories) {
			this.storyDAO.store(story);
		}
		for(Task task : organizationTO.tasks){
			this.taskDAO.store(task);
		}
		for(Assignment assignment : organizationTO.assignments) {
			this.assignmentDAO.store(assignment);
		}
		for(BacklogHourEntry backlogHourEntry : organizationTO.backlogHourEntries) {
			this.backlogHourEntryDAO.store(backlogHourEntry);
		}
		for(StoryHourEntry storyHourEntry : organizationTO.storyHourEntries) {
			this.storyHourEntryDAO.store(storyHourEntry);
		}
		for(TaskHourEntry taskHourEntry : organizationTO.taskHourEntries) {
			this.taskHourEntryDAO.store(taskHourEntry);
		}
		for(BacklogHistoryEntry backlogHistoryEntry : organizationTO.backlogHistoryEntries) {
			this.backlogHistoryEntryDAO.store(backlogHistoryEntry);
		}
		for(IterationHistoryEntry iterationHistoryEntry : organizationTO.iterationHistoryEntries) {
			this.iterationHistoryEntryDAO.store(iterationHistoryEntry);
		}
		for(Label label : organizationTO.labels) {
			this.labelDAO.store(label);
		}
		for(StoryAccess storyAccess : organizationTO.storyAccesses) {
			this.storyAccessDAO.store(storyAccess);
		}
		for(StoryRank storyRank : organizationTO.storyRanks) {
			this.storyRankDAO.store(storyRank);
		}
		for(Team team : organizationTO.teams) {
			this.teamDAO.store(team);
		}
		for(WhatsNextEntry whatsNextEntry : organizationTO.whatsNextEntries) {
			this.whatsNextEntryDAO.store(whatsNextEntry);
		}
		for(WhatsNextStoryEntry whatsNextStoryEntry : organizationTO.whatsNextStoryEntries) {
			this.whatsNextStoryEntryDAO.store(whatsNextStoryEntry);
		}
		for(WidgetCollection widgetCollection : organizationTO.widgetCollections) {
			this.widgetCollectionDAO.store(widgetCollection);
		}
		for(Setting setting : organizationTO.settings) {
			this.settingDAO.store(setting);
		}
	}
}

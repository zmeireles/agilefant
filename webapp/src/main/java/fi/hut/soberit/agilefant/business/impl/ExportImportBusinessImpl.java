package fi.hut.soberit.agilefant.business.impl;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.typesafe.config.Config;

import fi.hut.soberit.agilefant.business.ExportImportBusiness;
import fi.hut.soberit.agilefant.business.IterationBusiness;
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
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.User;

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
	private IterationBusiness iterationBusiness;
	
	@Autowired
	SessionFactory sessionFactory;
	
	private void addInOrder(Story story, Collection<Story> stories) {
		Story parent = story.getParent();
		if(parent!=null && !stories.contains(parent) && story!=parent) {
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
	
	private String generateReadonlyToken()
	{
		SecureRandom r = new SecureRandom();
		String token = new BigInteger(130, r).toString();

		int count = iterationBusiness.getIterationCountFromReadonlyToken(token);
		while(count > 0){
			r = new SecureRandom();
			token = new BigInteger(130, r).toString();
			count = iterationBusiness.getIterationCountFromReadonlyToken(token);
		}

		return token;
	}
	
	@Override
	@Transactional
	public void importOrganization(OrganizationDumpTO organizationTO) {
		for(User user : organizationTO.users) {
			user.setLoginName(user.getLoginName() + new Date().getTime());
		}
		for(Iteration iteration : organizationTO.iterations) {
			if (iteration.getReadonlyToken() != null) {
				iteration.setReadonlyToken(generateReadonlyToken());
			}
		}

		Collection<Object> objects = new ArrayList<Object>();
		objects.addAll(organizationTO.users);
		objects.addAll(organizationTO.holidays);
		objects.addAll(organizationTO.products);
		objects.addAll(organizationTO.projects);
		objects.addAll(organizationTO.iterations);
		objects.addAll(organizationTO.stories);
		objects.addAll(organizationTO.tasks);
		objects.addAll(organizationTO.assignments);
		objects.addAll(organizationTO.backlogHourEntries);
		objects.addAll(organizationTO.storyHourEntries);
		objects.addAll(organizationTO.taskHourEntries);
		objects.addAll(organizationTO.backlogHistoryEntries);
		objects.addAll(organizationTO.iterationHistoryEntries);
		objects.addAll(organizationTO.labels);
		objects.addAll(organizationTO.storyAccesses);
		objects.addAll(organizationTO.storyRanks);
		objects.addAll(organizationTO.teams);
		objects.addAll(organizationTO.whatsNextEntries);
		objects.addAll(organizationTO.whatsNextStoryEntries);
		objects.addAll(organizationTO.widgetCollections);
		objects.addAll(organizationTO.settings);

		Session session = this.sessionFactory.openSession();
		Transaction tx = session.beginTransaction(); 
		
		try {
			int index = 0;
			for(Object object : objects) {
				session.save(object);
				if(index++ % 50 == 0) {
					session.flush();
					session.clear();
				}
			}

			tx.commit();
		} catch(Exception e) {
			tx.rollback();
		} finally {
			session.close();			
		}
	}
}

package fi.hut.soberit.agilefant.business;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

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

public interface ExportImportBusiness {

	public OrganizationDumpTO exportOrganization();
	
	public void importOrganization(OrganizationDumpTO organizationTO);

	@JsonPropertyOrder({ "version", "users", "holidays", "products", "projects", "iterations", "stories", "tasks", "assignments", "backlogHourEntries",
		"storyHourEntries", "taskHourEntries", "backlogHistoryEntries", "iterationHistoryEntries", "labels", "storyAccesses", "storyRanks",
		"teams", "whatsNextEntries", "whatsNextStoryEntries", "widgetCollections", "settings"})
	public static class OrganizationDumpTO {

		public String version;
		
		public Collection<User> users;
		public Collection<Holiday> holidays;
		public Collection<Product> products;
		public Collection<Project> projects;
		public Collection<Iteration> iterations;
		public Collection<Story> stories;
		public Collection<Task> tasks;
		public Collection<Assignment> assignments;
		public Collection<BacklogHourEntry> backlogHourEntries;
		public Collection<StoryHourEntry> storyHourEntries;
		public Collection<TaskHourEntry> taskHourEntries;		
		public Collection<BacklogHistoryEntry> backlogHistoryEntries;
		public Collection<IterationHistoryEntry> iterationHistoryEntries;
		public Collection<Label> labels;
		public Collection<StoryAccess> storyAccesses;
		public Collection<StoryRank> storyRanks;
		public Collection<Team> teams;
		public Collection<WhatsNextEntry> whatsNextEntries;
		public Collection<WhatsNextStoryEntry> whatsNextStoryEntries;
		public Collection<WidgetCollection> widgetCollections;		
		public Collection<Setting> settings;
	}
}

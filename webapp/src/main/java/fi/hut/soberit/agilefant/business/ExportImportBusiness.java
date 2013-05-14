package fi.hut.soberit.agilefant.business;

import java.util.Collection;

import fi.hut.soberit.agilefant.model.Assignment;
import fi.hut.soberit.agilefant.model.BacklogHistoryEntry;
import fi.hut.soberit.agilefant.model.BacklogHourEntry;
import fi.hut.soberit.agilefant.model.Holiday;
import fi.hut.soberit.agilefant.model.HourEntry;
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

	public static class OrganizationDumpTO {

		public Collection<Assignment> assignments;
		public Collection<BacklogHistoryEntry> backlogHistoryEntries;
		public Collection<BacklogHourEntry> backlogHourEntries;
		public Collection<Holiday> holidays;
		public Collection<HourEntry> hourEntrys;
		public Collection<Iteration> iterations;
		public Collection<IterationHistoryEntry> iterationHistoryEntries;
		public Collection<Label> labels;
		public Collection<Product> products;
		public Collection<Project> projects;
		public Collection<Setting> settings;
		public Collection<Story> storys;
		public Collection<StoryAccess> storyAccesss;
		public Collection<StoryHourEntry> storyHourEntrys;
		public Collection<StoryRank> storyRanks;
		public Collection<Task> tasks;
		public Collection<TaskHourEntry> taskHourEntries;
		public Collection<Team> teams;
		public Collection<User> users;
		public Collection<WhatsNextEntry> whatsNextEntries;
		public Collection<WhatsNextStoryEntry> whatsNextStoryEntries;
		public Collection<WidgetCollection> widgetCollections;		
	}
}

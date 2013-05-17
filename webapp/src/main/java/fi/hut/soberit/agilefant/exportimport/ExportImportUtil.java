package fi.hut.soberit.agilefant.exportimport;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.joda.JodaModule;

import fi.hut.soberit.agilefant.business.ExportImportBusiness.OrganizationDumpTO;
import fi.hut.soberit.agilefant.model.Assignment;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogHistoryEntry;
import fi.hut.soberit.agilefant.model.BacklogHourEntry;
import fi.hut.soberit.agilefant.model.Holiday;
import fi.hut.soberit.agilefant.model.HolidayAnomaly;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.IterationHistoryEntry;
import fi.hut.soberit.agilefant.model.Label;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Project;
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

public class ExportImportUtil {

	private static final ObjectMapper objectMapper;
	
	static {
		objectMapper = new ObjectMapper();
    	SimpleModule importExportModule = new ImportExportModule();
    	
    	objectMapper.registerModule(importExportModule);
    	objectMapper.registerModule(new JodaModule());

    	objectMapper.setVisibility(PropertyAccessor.ALL, Visibility.NONE);
    	objectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);    	
    }
	
	private static ObjectMapper getObjectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
    	SimpleModule importExportModule = new ImportExportModule();
    	
    	objectMapper.registerModule(importExportModule);
    	objectMapper.registerModule(new JodaModule());

    	objectMapper.setVisibility(PropertyAccessor.ALL, Visibility.NONE);
    	objectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);    	

    	return objectMapper;
	}

	public static void toJson(OutputStream out, OrganizationDumpTO organizationTO) {
		try {
			getObjectMapper().writer(new DefaultPrettyPrinter()).writeValue(out, organizationTO);			
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static OrganizationDumpTO fromJson(InputStream in) {
		try {
	    	OrganizationDumpTO organizationTO = getObjectMapper().readValue(in, OrganizationDumpTO.class);			
	    	return organizationTO;
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@SuppressWarnings("serial")
	public static class ImportExportModule extends SimpleModule {
		public ImportExportModule() {
			super("ImportExport", new Version(0,0,1,null,null,null));			
		}
		
		@Override
		public void setupModule(SetupContext context) {
			context.setMixInAnnotations(Backlog.class, ExportableBacklog.class);
			context.setMixInAnnotations(Product.class, ExportableProduct.class);
			context.setMixInAnnotations(Project.class, ExportableProject.class);
			context.setMixInAnnotations(Iteration.class, ExportableIteration.class);
			context.setMixInAnnotations(User.class, ExportableUser.class);
			context.setMixInAnnotations(Story.class, ExportableStory.class);
			context.setMixInAnnotations(Task.class, ExportableTask.class);
		}
	}

	@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
	public interface ExportableBacklog {
	}
	
	public static abstract class ExportableUser extends User {
		@Override
		@JsonIgnore
		public abstract Collection<Team> getTeams();

		@Override
		@JsonIgnore
		public abstract Collection<Assignment> getAssignments();
		
		@Override
		@JsonIgnore
		public abstract Collection<Story> getStories();

		@Override
		@JsonIgnore
		public abstract Set<Task> getTasks();

		@Override
		@JsonIgnore
		public abstract Collection<Holiday> getHolidays();

		@Override
		@JsonIgnore
		public abstract Collection<HolidayAnomaly> getHolidayAnomalies();
		
	}
	
	public static abstract class ExportableProduct extends Product implements ExportableBacklog {

		@Override
		@JsonIgnore
		public abstract Collection<Team> getTeams();

		@Override
		@JsonIgnore
		public abstract Collection<Project> getProjects();

		@Override
		@JsonIgnore
		public abstract Collection<Iteration> getIterations();

		@Override
		@JsonIgnore
		public abstract Set<Backlog> getChildren();

		@Override
		@JsonIgnore
		public abstract Set<Story> getStories();

		@Override
		@JsonIgnore
		public abstract Set<BacklogHourEntry> getHourEntries();

		@Override
		@JsonIgnore
		public abstract Set<StoryRank> getStoryRanks();
	}
	
	public static abstract class ExportableProject extends Project implements ExportableBacklog {
		@Override
		@JsonIgnore
		public abstract Set<BacklogHistoryEntry> getBacklogHistoryEntries();
		
		@Override
		@JsonIgnore
		public abstract Set<Backlog> getChildren();

		@Override
		@JsonIgnore
		public abstract Set<Story> getStories();

		@Override
		@JsonIgnore
		public abstract Set<BacklogHourEntry> getHourEntries();

		@Override
		@JsonIgnore
		public abstract Set<StoryRank> getStoryRanks();

		@Override
		@JsonIgnore
		public abstract Set<Assignment> getAssignments();
	}
	
	public static abstract class ExportableIteration extends Iteration implements ExportableBacklog {

		@Override
		@JsonIgnore
		public abstract Set<Task> getTasks();

		@Override
		@JsonIgnore
		public abstract Set<Assignment> getAssignments();

		@Override
		@JsonIgnore
		public abstract Set<IterationHistoryEntry> getHistoryEntries();

		@Override
		@JsonIgnore
		public abstract Set<Story> getAssignedStories();

		@Override
		@JsonIgnore
		public abstract Collection<Team> getTeams();
		
		@Override
		@JsonIgnore
		public abstract Set<Backlog> getChildren();

		@Override
		@JsonIgnore
		public abstract Set<Story> getStories();

		@Override
		@JsonIgnore
		public abstract Set<BacklogHourEntry> getHourEntries();

		@Override
		@JsonIgnore
		public abstract Set<StoryRank> getStoryRanks();
	}

	public static abstract class ExportableStory extends Story {
		@Override
		@JsonIgnore
		public abstract Set<StoryAccess> getStoryAccesses();

		@Override
		@JsonIgnore
		public abstract Set<WhatsNextStoryEntry> getWhatsNextStoryEntries();

		@Override
		@JsonIgnore
		public abstract Set<Task> getTasks();

		@Override
		@JsonIgnore
		public abstract Set<StoryHourEntry> getHourEntries();

		@Override
		@JsonIgnore
		public abstract List<Story> getChildren();

		@Override
		@JsonIgnore
		public abstract Set<StoryRank> getStoryRanks();

		@Override
		@JsonIgnore
		public abstract Set<Label> getLabels();
	}
	
	public static abstract class ExportableTask extends Task {

		@Override
		@JsonIgnore
		public abstract Set<WhatsNextEntry> getWhatsNextEntries();

		@Override
		@JsonIgnore
		public abstract Set<TaskHourEntry> getHourEntries();
	}
}

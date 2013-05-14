package fi.hut.soberit.agilefant.exportimport;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
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
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.StoryAccess;
import fi.hut.soberit.agilefant.model.Team;
import fi.hut.soberit.agilefant.model.User;

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

	public static void toJson(OutputStream out, OrganizationDumpTO organizationTO) {
		try {
			objectMapper.writer(new DefaultPrettyPrinter()).writeValue(out, organizationTO);			
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static OrganizationDumpTO fromJson(InputStream in) {
		try {
	    	OrganizationDumpTO organizationTO = objectMapper.readValue(in, OrganizationDumpTO.class);			
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
			context.setMixInAnnotations(User.class, ExportableUser.class);
			context.setMixInAnnotations(Story.class, ExportableStory.class);
		}
	}

	@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
	public interface ExportableBacklog {
	}
	
	public static abstract class ExportableUser extends User {
		@Override
		@JsonIgnore
		public abstract Collection<Team> getTeams();
	}

	public static abstract class ExportableStory extends Story {
		@Override
		@JsonIgnore
		public abstract Set<StoryAccess> getStoryAccesses();
	}
}

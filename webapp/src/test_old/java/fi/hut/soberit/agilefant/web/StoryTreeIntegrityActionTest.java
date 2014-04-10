package fi.hut.soberit.agilefant.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.opensymphony.xwork2.Action;

import fi.hut.soberit.agilefant.business.BacklogBusiness;
import fi.hut.soberit.agilefant.business.IterationBusiness;
import fi.hut.soberit.agilefant.business.StoryBusiness;
import fi.hut.soberit.agilefant.business.StoryTreeIntegrityBusiness;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.test.Mock;
import fi.hut.soberit.agilefant.test.MockContextLoader;
import fi.hut.soberit.agilefant.test.MockedTestCase;
import fi.hut.soberit.agilefant.test.TestedBean;
import fi.hut.soberit.agilefant.transfer.MoveStoryNode;
import fi.hut.soberit.agilefant.util.StoryHierarchyIntegrityViolationType;
import fi.hut.soberit.agilefant.util.StoryTreeIntegrityMessage;

import static org.junit.Assert.*;

import static org.easymock.EasyMock.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = MockContextLoader.class)
public class StoryTreeIntegrityActionTest extends MockedTestCase {

    @TestedBean
    StoryTreeIntegrityAction testable;
    
    @Mock
    StoryTreeIntegrityBusiness storyTreeIntegrityBusiness;
    
    @Mock
    StoryBusiness storyBusiness;
    
    @Mock
    BacklogBusiness backlogBusiness;
    
    @Mock
    IterationBusiness iterationBusiness;

    @Test
    @DirtiesContext
    public void testCheckChangeBacklog() {
        Story story = new Story();
        Backlog newBacklog = new Project();
        MoveStoryNode data = new MoveStoryNode();
        
        testable.setStoryId(222);
        testable.setBacklogId(66);
        
        List<StoryTreeIntegrityMessage> messages = new ArrayList<StoryTreeIntegrityMessage>(
                Arrays.asList(new StoryTreeIntegrityMessage(null, null, null),
                        new StoryTreeIntegrityMessage(null, null, null)));
        
        expect(storyBusiness.retrieve(222)).andReturn(story);
        expect(backlogBusiness.retrieve(66)).andReturn(newBacklog);
        expect(storyTreeIntegrityBusiness.checkChangeBacklog(story, newBacklog))
            .andReturn(messages);
                
        expect(storyTreeIntegrityBusiness.generateChangedStoryTree(story, messages))
            .andReturn(data);
        
        expect(storyTreeIntegrityBusiness.hasParentStoryConflict(story, newBacklog))
            .andReturn(true);
        
        replayAll();
        assertEquals(Action.SUCCESS, testable.checkChangeBacklog());
        verifyAll();
        
        assertSame(data, testable.getData());
        assertSame(messages, testable.getMessages());
        assertTrue(testable.isParentStoryConflict());
    }
    
    @Test
    @DirtiesContext
    public void testCheckChangeBacklog_fatalError() {
        Story story = new Story();
        Backlog newBacklog = new Project();
        MoveStoryNode data = new MoveStoryNode();
        
        testable.setStoryId(222);
        testable.setBacklogId(66);
        
        StoryTreeIntegrityMessage fatal = new StoryTreeIntegrityMessage(null, null, StoryHierarchyIntegrityViolationType.MOVE_TO_ITERATION_HAS_CHILDREN);
        List<StoryTreeIntegrityMessage> messages = new ArrayList<StoryTreeIntegrityMessage>(
                Arrays.asList(fatal, new StoryTreeIntegrityMessage(null, null, null)));
        
        expect(storyBusiness.retrieve(222)).andReturn(story);
        expect(backlogBusiness.retrieve(66)).andReturn(newBacklog);
        expect(storyTreeIntegrityBusiness.checkChangeBacklog(story, newBacklog))
            .andReturn(messages);
        
        expect(storyTreeIntegrityBusiness.generateChangedStoryTree(story, messages))
            .andReturn(data);
        
        expect(storyTreeIntegrityBusiness.hasParentStoryConflict(story, newBacklog))
            .andReturn(false);
        
        replayAll();
        assertEquals("fatalConstraint", testable.checkChangeBacklog());
        verifyAll();
        
        assertTrue(testable.getMessages().contains(fatal));
        assertEquals(1, testable.getMessages().size());
        assertFalse(testable.isParentStoryConflict());
    }

    
    @Test
    @DirtiesContext
    public void testCheckChangeParentStory() {
        Story story = new Story();
        Story targetStory = new Story();
        
        testable.setStoryId(222);
        testable.setTargetStoryId(632);
        
        List<StoryTreeIntegrityMessage> messages = new ArrayList<StoryTreeIntegrityMessage>(
                Arrays.asList(new StoryTreeIntegrityMessage(null, null, null),
                        new StoryTreeIntegrityMessage(null, null, null)));
        
        expect(storyBusiness.retrieve(222)).andReturn(story);
        expect(storyBusiness.retrieve(632)).andReturn(targetStory);
        expect(storyTreeIntegrityBusiness.checkChangeParentStory(story, targetStory))
            .andReturn(messages);
        
        replayAll();
        assertEquals(Action.SUCCESS, testable.checkChangeParentStory());
        verifyAll();
        
        assertSame(messages, testable.getMessages());
    }
}

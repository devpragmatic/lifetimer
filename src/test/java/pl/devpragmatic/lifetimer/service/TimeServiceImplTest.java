package pl.devpragmatic.lifetimer.service;

import java.util.UUID;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import static org.mockito.Matchers.anyString;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import pl.devpragmatic.lifetimer.domain.Time;
import pl.devpragmatic.lifetimer.repository.TimeRepository;
import pl.devpragmatic.lifetimer.service.exception.ServiceException;

/**
 * @author devpragmatic
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class TimeServiceImplTest {

	@InjectMocks
	private final TimeService timeService = new TimeServiceImpl();

	@Mock
	private TimeRepository timeRepository;

        private Time time;
        private final String parentId = UUID.randomUUID().toString();
        
        @Before
        public void setUp() {
            time = new Time();
        }
    
	@After
	public void tearDown(){
            Mockito.reset(timeRepository);
	}

	@Test
	public void givenSomethingTimeWhenUseAddThenSaveIt() {
            time = Mockito.mock(Time.class);
            
            timeService.add(time);
            
            Mockito.verify(timeRepository).save(time);
        }
        
        @Test
        public void givenSomethingTimeWithParentWhenUseAddThenAddTimeToParent(){
            int days = 1, hours = 2, minutes = 3, seconds = 4;
            time = new Time();
            time.setTime(days, hours, minutes, seconds);
            time.setParentId(parentId);
            Time parent = Mockito.mock(Time.class);
            Mockito.when(timeRepository.findOneById(time.getParentId())).thenReturn(parent);
            
            timeService.add(time);
            
            Mockito.verify(timeRepository).save(time);
            Mockito.verify(timeRepository).findOneById(time.getParentId());
            Mockito.verify(parent).addTime(time);
            Mockito.verify(timeRepository).save(parent);
        }
        
        @Test
	public void givenSomethingTimeWhenUseDeleteThenDeleteIt() {
            time = Mockito.mock(Time.class);
            
            timeService.delete(time);
            
            Mockito.verify(timeRepository).delete(time);
        }
        
        @Test
        public void givenSomethingTimeWithParentWhenUseDeleteThenSubTimeFromParent(){
            int days = 4, hours = 3, minutes = 2, seconds = 1;
            time = new Time();
            time.setTime(days, hours, minutes, seconds);
            time.setParentId(parentId);
            Time parent = Mockito.mock(Time.class);
            Mockito.when(timeRepository.findOneById(time.getParentId())).thenReturn(parent);
            
            timeService.delete(time);
            
            Mockito.verify(timeRepository).delete(time);
            Mockito.verify(timeRepository).findOneById(time.getParentId());
            Mockito.verify(parent).subTime(time);
            Mockito.verify(timeRepository).save(parent);
        }
        
        @Test(expected = ServiceException.class)
        public void givenTimeWithWrongParentIdWhenUseAddThenThrowServiceException(){
            time = new Time();
            time.setParentId(parentId);
            Mockito.when(timeRepository.findOneById(anyString())).thenReturn(null);
            
            timeService.add(time);
        }
        
        @Test(expected = ServiceException.class)
        public void givenTimeWithWrongParentIdWhenUseDeleteThenThrowServiceException(){
            time = new Time();
            time.setParentId(parentId);
            Mockito.when(timeRepository.findOneById(anyString())).thenReturn(null);
            
            timeService.delete(time);
        }
}
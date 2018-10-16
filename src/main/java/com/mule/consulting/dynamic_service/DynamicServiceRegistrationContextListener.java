package com.mule.consulting.dynamic_service;

import java.util.Collections;

import javax.inject.Inject;

import org.mule.runtime.api.artifact.Registry;
import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.api.message.Message;
import org.mule.runtime.core.api.construct.Flow;
import org.mule.runtime.core.api.context.notification.MuleContextNotification;
import org.mule.runtime.core.api.context.notification.MuleContextNotificationListener;
import org.mule.runtime.core.api.event.CoreEvent;
import org.mule.runtime.core.api.event.EventContextFactory;
import org.mule.runtime.dsl.api.component.config.DefaultComponentLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DynamicServiceRegistrationContextListener
		implements MuleContextNotificationListener<MuleContextNotification> {

	protected static final Logger logger = LoggerFactory.getLogger(DynamicServiceRegistrationContextListener.class);

	@Inject
	private Registry muleRegistry;
	
    private String registrationFlowName;
    private String deregistrationFlowName;
    private String heartbeatFlowName;
    
    public DynamicServiceRegistrationContextListener() {
    }

    public void onNotification(final MuleContextNotification notification)
    {
        if (notification.getAction().getActionId() == MuleContextNotification.CONTEXT_STARTED)
        {
            startService();
        }
        else if (notification.getAction().getActionId() == MuleContextNotification.CONTEXT_STOPPING)
        {
        	stopService();
        }
    }
    
	private void stopService() {
		try {
			Flow flow = lookupFlow(getHeartbeatFlowName());
			if (flow != null) {
				flow.stop();
			} else {
				logger.warn(" heartbeat flow does not exist: " + getHeartbeatFlowName());
			}
		} catch (MuleException ex) {
			logger.error("Error while trying to stop heartbeat ", ex);
		}

		try {
			Flow flow = lookupFlow(getDeregistrationFlowName());
			if (flow != null) {
				Message msg = Message.builder().nullValue()
						.attributesValue(Collections.singletonMap("flowName", getDeregistrationFlowName())).build();

				CoreEvent event = CoreEvent.builder(
						EventContextFactory.create(flow, DefaultComponentLocation.fromSingleComponent("DynamicServiceRegistration_stopService")))
						.message(msg).build();

				flow.process(event);
			} else {
				logger.warn(" Stop service flow does not exist: " + getDeregistrationFlowName());
			}
		} catch (MuleException ex) {
			logger.error("Error while trying to stop service ", ex);
		}
	}
	
	private void startService() {
		try {
			Flow flow = lookupFlow(getRegistrationFlowName());
			if (flow != null) {
				Message msg = Message.builder().nullValue()
						.attributesValue(Collections.singletonMap("flowName", getRegistrationFlowName())).build();

				CoreEvent event = CoreEvent.builder(
						EventContextFactory.create(flow, DefaultComponentLocation.fromSingleComponent("DynamicServiceRegistration_startService")))
						.message(msg).build();

				flow.process(event);
				Thread.sleep(5000); //pause before starting heartbeat
			} else {
				logger.warn(" Start service flow does not exist: " + getRegistrationFlowName());
			}
		} catch (Exception ex) {
			logger.error("Error while trying to start service ", ex);
		}

		try {
			Flow flow = lookupFlow(getHeartbeatFlowName());
			if (flow != null) {
				flow.start();
			} else {
				logger.warn(" heartbeat flow does not exist: " + getHeartbeatFlowName());
			}
		} catch (MuleException ex) {
			logger.error("Error while trying to start heartbeat ", ex);
		}
	}

	private Flow lookupFlow(String flowName) {
		return (Flow) muleRegistry.lookupByName(flowName).orElse(null);
	}

	public String getRegistrationFlowName() {
		return registrationFlowName;
	}

	public void setRegistrationFlowName(String registrationFlowName) {
		this.registrationFlowName = registrationFlowName;
	}

	public String getDeregistrationFlowName() {
		return deregistrationFlowName;
	}

	public void setDeregistrationFlowName(String deregistrationFlowName) {
		this.deregistrationFlowName = deregistrationFlowName;
	}

	public String getHeartbeatFlowName() {
		return heartbeatFlowName;
	}

	public void setHeartbeatFlowName(String heartbeatFlowName) {
		this.heartbeatFlowName = heartbeatFlowName;
	}
}

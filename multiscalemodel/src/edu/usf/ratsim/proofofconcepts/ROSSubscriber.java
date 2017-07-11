package edu.usf.ratsim.proofofconcepts;

import org.apache.commons.logging.Log;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.DefaultNodeMainExecutor;
import org.ros.node.Node;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMain;
import org.ros.node.NodeMainExecutor;
import org.ros.node.topic.Subscriber;

public class ROSSubscriber implements NodeMain {

  @Override
  public GraphName getDefaultNodeName() {
    return GraphName.of("NodoJava");
  }

  @Override
  public void onStart(ConnectedNode node) {
		Log log = node.getLog();
	    Subscriber<std_msgs.String> subscriber = node.newSubscriber("chatter", std_msgs.String._TYPE);
	    subscriber.addMessageListener(new MessageListener<std_msgs.String>() {
	      @Override
	      public void onNewMessage(std_msgs.String message) {
	        log.info("I heard: \"" + message.getData() + "\"");
	      }
	    });
  }

  @Override
  public void onShutdown(Node node) {
  }

  @Override
  public void onShutdownComplete(Node node) {
  }

  @Override
  public void onError(Node node, Throwable throwable) {
  }
  
  public static void main(String[] args){
	  NodeMainExecutor exec = DefaultNodeMainExecutor.newDefault();
	  NodeConfiguration conf = NodeConfiguration.newPublic("localhost");
	  exec.execute(new ROSSubscriber(), conf);
  }

}
package edu.usf.experiment.deprecated.plot;
//package edu.usf.experiment.Deprecated.plot;
//
//import java.lang.reflect.Constructor;
//import java.lang.reflect.InvocationTargetException;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Map;
//
//import edu.usf.experiment.utils.ElementWrapper;
//
//public class PlotterLoader {
//
//	private static PlotterLoader instance;
//	private Map<String, Class<?>> classBySimpleName;
//
//	public static PlotterLoader getInstance() {
//		if (instance == null)
//			instance = new PlotterLoader();
//		return instance;
//	}
//
//	private PlotterLoader() {
//
//	}
//
//	public List<Plotter> load(ElementWrapper plotterNodes, String logPath) {
//		List<Plotter> res = new LinkedList<Plotter>();
//		if (plotterNodes != null) {
//			List<ElementWrapper> plotterList = plotterNodes
//					.getChildren("plotter");
//			for (ElementWrapper plotterNode : plotterList) {
//				try {
//					Constructor constructor;
//					// constructor = classBySimpleName.get(
//					// plotterNode.getChildText("name")).getConstructor(
//					// ElementWrapper.class);
//					constructor = Class.forName(
//							plotterNode.getChildText("name")).getConstructor(
//							ElementWrapper.class, String.class);
//					Plotter plotter = (Plotter) constructor
//							.newInstance(plotterNode.getChild("params"), logPath);
//					res.add(plotter);
//				} catch (NoSuchMethodException  e) {
//					e.printStackTrace();
//				} catch (InstantiationException e) {
//					e.printStackTrace();
//				} catch (IllegalAccessException e) {
//					e.printStackTrace();
//				} catch (IllegalArgumentException e) {
//					e.printStackTrace();
//				} catch (InvocationTargetException e) {
//					e.printStackTrace();
//				} catch (ClassNotFoundException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		}
//		return res;
//	}
//
//}

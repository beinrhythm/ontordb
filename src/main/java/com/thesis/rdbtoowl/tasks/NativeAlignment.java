/*package com.thesis.rdbtoowl.tasks;
*//**
 * @author Uthayasanker.T
 * @lab THINC Lab
 * Computer Science, University of Georgia.
 *//*

import java.io.File;
import java.io.IOException;
import java.net.URL;

import edu.uga.cs.optima.exception.OptimaException;
import edu.uga.cs.optima.graph.OntGraph;
import edu.uga.cs.optima.graph.OntGraphBuilder;
import edu.uga.cs.thinc.optima.Main.OptimaDefault;
import edu.uga.cs.thinc.optima.core.AbstractOptima;
import edu.uga.cs.thinc.optima.core.EdgeMatcher;
import edu.uga.cs.thinc.optima.core.Memory;
import edu.uga.cs.thinc.optima.core.MixtureModel;
import edu.uga.cs.thinc.optima.core.OntologySimilarityStore;
import edu.uga.cs.thinc.optima.core.SeedGenerator;
import edu.uga.cs.thinc.optima.core.impl.DefaultEdgeMather;
import edu.uga.cs.thinc.optima.core.impl.DefaultSampleGenerator;
import edu.uga.cs.thinc.optima.core.impl.EmptyProgressMonitor;
import edu.uga.cs.thinc.optima.core.impl.PostProcessorImpl;
import edu.uga.cs.thinc.optima.core.impl.SEALSeedGenerator;
import edu.uga.cs.thinc.optima.core.impl.ThresholdedSeedGenerator;
import edu.uga.cs.thinc.optima.util.MixtureModelPrinter;
import edu.uga.cs.thinc.optima.util.OptimaAlignment;
import edu.uga.cs.thinc.util.PropertyManager;


public class NativeAlignment extends AbstractOptima {
	private static final int LARGE_ONTO_SIZE = 1000;
	//private final String root;
	boolean isSame = false;
	OntGraph graph1;
	OntGraph graph2;
	private final String outputFileName;
	public NativeAlignment(String src, String target, String outputFileName) throws OptimaException {
		this(src, target, null, outputFileName);
	}
	
	public NativeAlignment(String paramString1, String paramString2, URL paramURL, String outputFileName)
			throws OptimaException {
		this.outputFileName = outputFileName;
		if(paramString1.equals(paramString2)){
			isSame = true;
		}
		//this.root = root;
		OptimaDefault.loadDefaultPropertiesNumerical();
		loadLocationPropertiesForSEAL();
		OptimaDefault.initDefaultLogger();
		setup(paramString1, paramString2, paramURL);
		this.sage = true;
		this.useAnonSim = true;
    }
	public NativeAlignment(String root, String paramString1, String paramString2, URL paramURL, String outputFileName, String logFile)
			throws OptimaException, IOException {
		this.outputFileName = outputFileName;
		if(paramString1.equals(paramString2)){
			isSame = true;
		}
		OptimaDefault.loadDefaultPropertiesNumerical();
		loadLocationPropertiesForSEAL();
		OptimaDefault.initFileLogger(logFile);
		setup(paramString1, paramString2, paramURL);
		this.sage = true;
		this.useAnonSim = true;
    }
	public URL run()
			throws OptimaException
			{
		int i = 5;
		int j = 50;
		MixtureModel mModel = run(j, i);
		EdgeMatcher localEdgeMatcher = Memory.getInstance().getEdgeMatcher();
		logger.info("Generating edge matches from final alignment...");
		localEdgeMatcher.generateEdgeMatches(mModel);
		logger.info("Post processing final alignment...");
		PostProcessorImpl postProcessor = new PostProcessorImpl();
		mModel = postProcessor.process(mModel, Memory.getInstance().getSeedGenerator().getSeed());
		postProcessor.process(localEdgeMatcher.getObjectSeeds(), Memory.getInstance().getOntologySimilarityStore().getObjectEdgeSimilarityStrore(), Memory.getInstance().getModelGraph().getObjectProperties(), Memory.getInstance().getDataGraph().getObjectProperties());
		postProcessor.process(localEdgeMatcher.getOneofSeeds(), Memory.getInstance().getOntologySimilarityStore().getDataOneOfEdgeSimilarityStrore(), Memory.getInstance().getModelGraph().getOneOfDataProperties(), Memory.getInstance().getDataGraph().getOneOfDataProperties());
		postProcessor.process(localEdgeMatcher.getXsdSeeds(), Memory.getInstance().getOntologySimilarityStore().getDataXSDEdgeSimilarityStrore(), Memory.getInstance().getModelGraph().getXSDDataProperties(), Memory.getInstance().getDataGraph().getXSDDataProperties());
		postProcessor.process(localEdgeMatcher.getXSD_objectSeeds(), Memory.getInstance().getOntologySimilarityStore().getDataXSD_ObjectSimilarityStrore(), Memory.getInstance().getModelGraph().getXSDDataProperties(), Memory.getInstance().getDataGraph().getObjectProperties());
		postProcessor.process(localEdgeMatcher.getObject_XSDSeeds(), Memory.getInstance().getOntologySimilarityStore().getObject_XSDSimilarityStrore(), Memory.getInstance().getModelGraph().getObjectProperties(), Memory.getInstance().getDataGraph().getXSDDataProperties());
		OptimaAlignment optimaAlignment = MixtureModelPrinter.toAlignment(Memory.getInstance().getModelGraph(), Memory.getInstance().getDataGraph(), mModel, localEdgeMatcher);
		postProcessor.process(optimaAlignment);
		logger.info("Wrting output...");
	
		try {
			MixtureModelPrinter.writeToOAEI_Format(Memory.getInstance().getModelGraph(), 
					Memory.getInstance().getDataGraph(), optimaAlignment, outputFileName, false);
			File outFile = new File(outputFileName);
			logger.info("Output is written to "+ outFile +".");
			return outFile.toURI().toURL();
		} catch (IOException localIOException) {
			logger.error("Unable to write output.", localIOException);
			throw new OptimaException("Unable to write output.", localIOException);
		}
	}

	public void init(String paramString1, String paramString2)
	{
	}

	private void setup(String paramString1, String paramString2, URL paramURL)
			throws OptimaException
			{
		logger.info("Reading ontologies...");
		OntGraphBuilder grapgBuilder = new OntGraphBuilder();
		graph1 = grapgBuilder.getReadOnlyGraphSmart(paramString1);
		logger.info("-----------------------------------------------");
		graph2 = grapgBuilder.getReadOnlyGraphSmart(paramString2);
		if(graph1.getDefaultNameSpace()!=null && graph2.getDefaultNameSpace()!=null 
				&& graph1.getDefaultNameSpace().equals(graph2.getDefaultNameSpace())){
			isSame  = true;
		}
		boolean isLargeOnto = graph1.getNodeCount() > LARGE_ONTO_SIZE || graph2.getNodeCount() > LARGE_ONTO_SIZE;
		OntologySimilarityStore ontologySimStore = OptimaDefault.buildSimilarityStores(graph1, graph2, true, isLargeOnto);
		logger.info("Initialize SeedGenerator: " + ThresholdedSeedGenerator.class);
		SeedGenerator seedGenerator;
		if (paramURL == null)
			seedGenerator = new ThresholdedSeedGenerator(ontologySimStore.getNodeSimilarityStrore(), Double.parseDouble(PropertyManager.getMainConfig().getProperty("seed_threshold")));
		else
			try
		{
				seedGenerator = new SEALSeedGenerator(ontologySimStore.getNodeSimilarityStrore(), Double.parseDouble(PropertyManager.getMainConfig().getProperty("seed_threshold")), paramURL);
		}
		catch (Exception localException)
		{
			throw new OptimaException("Invalid input: partial alignment.", localException);
		}
		boolean randomized = false;
		logger.info("Initialize SampleGenerator:" + DefaultSampleGenerator.class + " <randomized: " + randomized + ">");
		DefaultSampleGenerator sampleGenerator = new DefaultSampleGenerator(randomized);
		EmptyProgressMonitor progressMonitor = new EmptyProgressMonitor();
		DefaultEdgeMather edgeMather = new DefaultEdgeMather(0.9D, 0.85D, 0.9D, 0.95D, 0.95D);
		Memory.init(graph1, graph2, (SeedGenerator)seedGenerator, sampleGenerator, ontologySimStore, edgeMather, progressMonitor);
		edgeMather.init();
	}

	public void loadLocationPropertiesForSEAL()	{
		PropertyManager config = PropertyManager.getMainConfig();
		String root ="./res/";
		//config.setProperty("ic_file", str + "WordNet/3.0/WordNet-InfoContent-3.0/ic-treebank-resnik-add1.dat");
		config.setProperty("ic_file", root  + "WordNet/3.0/WordNet-InfoContent-3.0/ic-brown-resnik-add1.dat");
		config.setProperty("WNNGram", root + "WordNet/3.0/WordNet-InfoContent-3.0/wn30compounds.txt");
		config.setProperty("wordnet", root + "WordNet/3.0/dict/");
		config.setProperty("wordnet_config", root + "file_properties.xml");
		config.setProperty("stop_file", root + "WordNet/3.0/WordNet-InfoContent-3.0/stoplist2.txt");
	}

	public URL sameGraph() throws IOException {
		MixtureModelPrinter.wrieToOAEISameGraph(graph1, graph2, outputFileName);
		File outFile = new File(outputFileName);
		logger.info("Output is written.");
		return outFile.toURI().toURL();
	}
	
	public static void main(String[] args) throws OptimaException {
		String outFile = "output1.rdf";
		String[] args1 
		= {"-s", "file:/Users/abhi.pandey/Documents/COMPANY.owl", 
				"-t", "file:/Users/abhi.pandey/Documents/EMPLOYEE.owl", 
				"-o", outFile , "--sage"};
		long start = System.currentTimeMillis();
		NativeAlignment optima = new NativeAlignment(args1[1], args1[3], outFile);
		optima.run();
		start = System.currentTimeMillis()-start;		
	}
}

*/
/************************************************************************
 *         Copyright (C) 2006-2007 The University of Sheffield          *
 *      Developed by Mark A. Greenwood <m.greenwood@dcs.shef.ac.uk>     *
 *                                                                      *
 * This program is free software; you can redistribute it and/or modify *
 * it under the terms of the GNU General Public License as published by *
 * the Free Software Foundation; either version 2 of the License, or    *
 * (at your option) any later version.                                  *
 *                                                                      *
 * This program is distributed in the hope that it will be useful,      *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of       *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the        *
 * GNU General Public License for more details.                         *
 *                                                                      *
 * You should have received a copy of the GNU General Public License    *
 * along with this program; if not, write to the Free Software          *
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.            *
 ************************************************************************/

package shef.nlp.wordnet.similarity;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import umbc.csee.ebiquity.ontologymatcher.config.Configuration;

import net.didion.jwnl.JWNL;


/**
 * An implementation of the WordNet similarity measure developed by Jiang and
 * Conrath. For full details of the measure see: <blockquote>Jiang J. and
 * Conrath D. 1997. Semantic similarity based on corpus statistics and lexical
 * taxonomy. In Proceedings of International Conference on Research in
 * Computational Linguistics, Taiwan.</blockquote>
 * 
 * @author Mark A. Greenwood
 */
public class ICSimilarity {
	static SimilarityMeasure sim = null;

	/**
	 * Instances of this similarity measure should be generated using the
	 * factory methods of {@link SimilarityMeasure}.
	 */
	protected ICSimilarity() {
		// A protected constructor to force the use of the newInstance method
	}

	public static SimilarityMeasure newInstance() throws Exception {

		if (sim == null) {
			String wordnetHome = Configuration.getWnConfigHomePath();
			System.out.println("Wordnet Home: " + wordnetHome);
			JWNL.initialize(new FileInputStream(wordnetHome + "wordnet.xml"));

			// Create a map to hold the similarity config params
			Map<String, String> params = new HashMap<String, String>();

			// the simType parameter is the class name of the measure to use
			params.put("simType", "shef.nlp.wordnet.similarity.Lin");

			// this param should be the URL to an infocontent file (if required
			// by the similarity measure being loaded)
			params.put("infocontent", "file:" + wordnetHome + "ic-bnc-resnik-add1.dat");

			// this param should be the URL to a mapping file if the
			// user needs to make synset mappings
			params.put("mapping", "file:" + wordnetHome + "domain_independent.txt");

			// create the similarity measure
			// SimilarityMeasure sim = SimilarityMeasure.newInstance(params);
			sim = SimilarityMeasure.newInstance(params);
		}

		return sim;
	}
}

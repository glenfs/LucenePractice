import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.spell.Dictionary;
import org.apache.lucene.search.spell.LuceneDictionary;
import org.apache.lucene.search.suggest.Lookup;
import org.apache.lucene.search.suggest.analyzing.AnalyzingInfixSuggester;
import org.apache.lucene.search.suggest.analyzing.AnalyzingSuggester;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by GXS8916 on 2/18/2016.
 */
public class LookupSuggestions
{

    // Get suggestions given a prefix and a region.
    private static void lookup(AnalyzingInfixSuggester suggester, String name,
                               String region) {
        try {
            List<Lookup.LookupResult> results;
            HashSet<BytesRef> contexts = new HashSet<BytesRef>();
            contexts.add(new BytesRef(region.getBytes("UTF8")));
            // Do the actual lookup.  We ask for the top 2 results.
            results = suggester.lookup(name, contexts, 2, true, false);
            System.out.println("-- \"" + name + "\" (" + region + "):");
            for (Lookup.LookupResult result : results) {
                System.out.println(result.key);
                TypeAheadCorpus p = getTypeAhead(result);
                if (p != null) {
                    System.out.println("  # sold: " + p.getWeight());
                }
            }
        } catch (IOException e) {
            System.err.println("Error");
        }
    }

    // Deserialize a Product from a LookupResult payload.
    private static TypeAheadCorpus getTypeAhead(Lookup.LookupResult result)
    {
        try {
            BytesRef payload = result.payload;
            if (payload != null) {
                ByteArrayInputStream bis = new ByteArrayInputStream(payload.bytes);
                ObjectInputStream in = new ObjectInputStream(bis);
                TypeAheadCorpus p = (TypeAheadCorpus) in.readObject();
                return p;
            } else {
                return null;
            }
        } catch (IOException|ClassNotFoundException e) {
            throw new Error("Could not decode payload :(");
        }
    }

    public void searchSuggestions() throws IOException {
        Directory directory = FSDirectory.open(new File("c://Ford//indexdata//LuceneTestindex7//"));
        IndexReader indexReader = DirectoryReader.open(directory);



        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        Dictionary dictionary = new LuceneDictionary(indexReader, "term");

        AnalyzingSuggester analyzingSuggester = new AnalyzingSuggester(new StandardAnalyzer());
        analyzingSuggester.build(dictionary);

        List<Lookup.LookupResult> lookupResultList = analyzingSuggester.lookup("humpty dum", false, 10);

        for (Lookup.LookupResult lookupResult : lookupResultList) {
            System.out.println(lookupResult.key + ": " + lookupResult.value);
        }
    }
}

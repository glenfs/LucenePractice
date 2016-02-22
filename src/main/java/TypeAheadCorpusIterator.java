/**
 * Created by GXS8916 on 2/18/2016.
 */
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.apache.lucene.search.suggest.InputIterator;
import org.apache.lucene.util.BytesRef;


public class TypeAheadCorpusIterator implements InputIterator
{
    private Iterator<TypeAheadCorpus> TypeAheadCorpusIterator;
    private TypeAheadCorpus currentTypeAheadCorpus;

    TypeAheadCorpusIterator(Iterator<TypeAheadCorpus> TypeAheadCorpusIterator) {
        this.TypeAheadCorpusIterator = TypeAheadCorpusIterator;
    }

    public boolean hasContexts() {
        return false;
    }

    public boolean hasPayloads() {
        return true;
    }

    public Comparator<BytesRef> getComparator() {
        return null;
    }

    // This method needs to return the key for the record; this is the
    // text we'll be autocompleting against.
    public BytesRef next() {
        if (TypeAheadCorpusIterator.hasNext()) {
            currentTypeAheadCorpus = TypeAheadCorpusIterator.next();
            try {
                return new BytesRef(currentTypeAheadCorpus.getTerm().getBytes("UTF8"));
            } catch (UnsupportedEncodingException e) {
                throw new Error("Couldn't convert to UTF-8");
            }
        } else {
            return null;
        }
    }

    // This method returns the payload for the record, which is
    // additional data that can be associated with a record and
    // returned when we do suggestion lookups.  In this example the
    // payload is a serialized Java object representing our TypeAheadCorpus.
    public BytesRef payload() {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(currentTypeAheadCorpus);
            out.close();
            return new BytesRef(bos.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            throw new Error("Well that's unfortunate.");
        }
    }

    // This method returns the contexts for the record, which we can
    // use to restrict suggestions.  In this example we use the
    // regions in which a TypeAheadCorpus is sold.

    public Set<BytesRef> contexts() {
        try {
            Set<BytesRef> regions = new HashSet();

                regions.add(new BytesRef("US".getBytes("UTF8")));

            return regions;
        } catch (UnsupportedEncodingException e) {
            throw new Error("Couldn't convert to UTF-8");
        }
    }

    // This method helps us order our suggestions.  In this example we
    // use the number of TypeAheadCorpuss of this type that we've sold.

    public long weight()
    {
        System.out.println(currentTypeAheadCorpus.getWeight());
        return (long)currentTypeAheadCorpus.getWeight();
    }
}


/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wikipedia_solr;

import org.apache.solr.handler.dataimport.Transformer;
import java.util.List;
import java.util.Map;
import org.apache.solr.handler.dataimport.Context;

/**
 *
 * @author patrickmullen
 */
public class PaddyTransformer extends Transformer {

    public Map<String, Object> transformRow(Map<String, Object> row, Context context) {
        List<Map<String, String>> fields = context.getAllEntityFields();

        for (Map<String, String> field : fields) {
            // Check if this field has trim="true" specified in the data-config.xml
            String trim = field.get("trim");
            if ("true".equals(trim)) {
                // Apply trim on this field
                String columnName = field.get("column");
                // Get this field's value from the current row
                String value = (String) row.get(columnName);
                // Trim and put the updated value back in the current row
                if (value != null) {
                    row.put(columnName, value.trim());
                }
            }
        }

        return row;
    }
}

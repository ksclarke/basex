package org.basex.query.func.fn;

import org.basex.query.*;
import org.basex.query.expr.CmpV.OpV;
import org.basex.query.value.item.*;
import org.basex.util.*;

/**
 * Function implementation.).
 *
 * @author BaseX Team 2005-14, BSD License
 * @author Christian Gruen
 */
public final class FnMin extends MinMax {
  @Override
  public Item item(final QueryContext qc, final InputInfo ii) throws QueryException {
    return minmax(OpV.GT, qc);
  }
}

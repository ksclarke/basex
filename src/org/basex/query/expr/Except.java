package org.basex.query.expr;

import static org.basex.query.QueryTokens.*;
import org.basex.query.QueryContext;
import org.basex.query.QueryException;
import org.basex.query.item.Item;
import org.basex.query.item.Nod;
import org.basex.query.iter.Iter;
import org.basex.query.iter.NodIter;
import org.basex.query.iter.NodeIter;
import org.basex.query.util.Err;

/**
 * Except Expression.
 *
 * @author Workgroup DBIS, University of Konstanz 2005-09, ISC License
 * @author Christian Gruen
 * @author Dennis Stratmann
 */
public final class Except extends Arr {
  /**
   * Constructor.
   * @param e expression list
   */
  public Except(final Expr[] e) {
    super(e);
  }

  @Override
  public NodeIter iter(final QueryContext ctx) throws QueryException {
    final Iter[] iter = new Iter[expr.length];
    for(int e = 0; e != expr.length; e++) iter[e] = ctx.iter(expr[e]);
    return duplicates(ctx) ? eval(iter) : iter(iter);
  }
  
  /**
   * Creates an except iterator.
   * @param iter iterators
   * @return resulting iterator
   */
  private NodeIter iter(final Iter[] iter) {
    return new NodeIter() {
      Nod[] items;

      @Override
      public Nod next() throws QueryException {
        if(items == null) { 
          items = new Nod[iter.length];
          for(int i = 0; i != iter.length; i++) next(i);
        }

        for(int i = 1; i != items.length; i++) {
          if (items[0] == null) return null;
          if (items[i] == null) continue;
          final int d = items[0].diff(items[i]);
          
          if(d < 0) {
            if(i + 1 == items.length) {
              break;
            }
          }
          if (d == 0) {
            next(0);
            i = 0;
          }
          if(d > 0) {
            next(i--);
          }
        }
        final Nod temp = items[0]; 
        next(0);
        return temp;
      }
      
      private void next(final int i) throws QueryException {
        final Item it = iter[i].next();
        if(it != null && !it.node()) Err.nodes(Except.this);
        items[i] = (Nod) it;
      }
    };
  }

  /**
   * Evaluates the iterators.
   * @param iter iterators
   * @return resulting iterator
   * @throws QueryException query exception
   */
  private NodeIter eval(final Iter[] iter) throws QueryException {
    NodIter ni = new NodIter(true);

    Item it;
    while((it = iter[0].next()) != null) {
      if(!it.node()) Err.nodes(this);
      ni.add((Nod) it);
    }
    
    for(int e = 1; e != expr.length; e++) {
      final Iter ir = iter[e];
      while((it = ir.next()) != null) {
        if(!(it.node())) Err.nodes(this);
        final Nod node = (Nod) it;
        for(int s = 0; s < ni.size; s++) {
          if(ni.item[s].is(node)) ni.delete(s--);
        }
      }
    }
    return ni;
  }
  
  @Override
  public String toString() {
    return "(" + toString(" " + EXCEPT + " ") + ")";
  }
}

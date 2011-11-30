package com.greenlaw110.rythm.internal.parser.build_in;

import com.greenlaw110.rythm.exception.ParseException;
import com.greenlaw110.rythm.internal.parser.BlockCodeToken;
import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.internal.parser.PatternStr;
import com.greenlaw110.rythm.spi.IContext;
import com.greenlaw110.rythm.spi.IParser;
import com.greenlaw110.rythm.util.TextBuilder;
import com.stevesoft.pat.Regex;

/**
 * <ul>Recognised the following patterns:
 * <li><code>@}? else if (...) {?...@}? </code></li>
 * <li><code>@ else ...@</code><li>
 * 
 * @author luog
 *
 */
public class ElseIfParser extends CaretParserFactoryBase {

    @Override
    public IParser create(final IContext ctx) {
        return new ParserBase(ctx) {

            @Override
            public TextBuilder go() {
                String a = dialect().a();
                Regex r1 = new Regex(String.format("^(%s\\}?\\s*(else\\s*if\\s*" + PatternStr.Expression + "\\s*\\{?)).*", a));
                Regex r2 = new Regex(String.format("^(%s\\}?\\s*(else([\\s\\r\\n\\t])+)).*", a));
                
                String s = ctx.getRemain();
                String s1 = null;
                if (r1.search(s)) {
                    s1 = r1.stringMatched(1);
                } else if (r2.search(s)) {
                    s1 = r2.stringMatched(1);
                }
                if (null == s1) return null;
                ctx.step(s1.length());
                s1 = r2.stringMatched(2);
                if (!s1.endsWith("{")) s1 = s1 + "{";
                if (!s1.startsWith("{")) s1 = "}" + s1;
                try {
                    ctx.closeBlock();
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                return new BlockCodeToken(s1, ctx);
            }
            
        };
    }

    public static void main(String[] args) {
        Regex r1 = new Regex(String.format("^(%s\\}?\\s*(else\\s*if\\s*" + PatternStr.Expression + "\\s*\\{?)).*", "@"));
        Regex r2 = new Regex(String.format("^(%s\\}?\\s*(else([\\s\\r\\n\\t])+)).*", "@"));
        String s = "@} else if (X.y[z.a].foo()) {<h1>good</h1>...";
        if (r1.search(s)) {
            System.out.println(r1.stringMatched(1));
            System.out.println(r1.stringMatched(2));
        }
        
        s = "@else if (X.y[z.a].foo()) <h1>good</h1>";
        if (r1.search(s)) {
            System.out.println(r1.stringMatched(1));
            System.out.println(r1.stringMatched(2));
        }
        
        s = "@else <h1>abc</h1>";
        if (r1.search(s)) {
            System.out.println(r1.stringMatched(1));
            System.out.println(r1.stringMatched(2));
        }
        if (r2.search(s)) {
            System.out.println(r2.stringMatched(1));
            System.out.println(r2.stringMatched(2));
        }
        
        s = "@ else \r\n     <td>@item.getChange()</td>\r\n     <td>@item.getRatio()</td>\r\n     @\r\n    </tr>\r\n@\r\n   </tbody>\r\n  </table>\r\n\r\n </body>\r\n</html>\r\n";
        if (r1.search(s)) {
            System.out.println(r1.stringMatched());
            System.out.println(r1.stringMatched(1));
        }
        if (r2.search(s)) {
            System.out.println(r2.stringMatched());
            System.out.println(r2.stringMatched(1));
            System.out.println(r2.stringMatched(2));
        }
    }
}
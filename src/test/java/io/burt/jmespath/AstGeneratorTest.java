package io.burt.jmespath;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import io.burt.jmespath.Query;
import io.burt.jmespath.ast.FieldNode;
import io.burt.jmespath.ast.ChainNode;
import io.burt.jmespath.ast.PipeNode;
import io.burt.jmespath.ast.IndexNode;
import io.burt.jmespath.ast.SliceNode;
import io.burt.jmespath.ast.FlattenNode;
import io.burt.jmespath.ast.SelectionNode;
import io.burt.jmespath.ast.SequenceNode;
import io.burt.jmespath.ast.ListWildcardNode;
import io.burt.jmespath.ast.HashWildcardNode;
import io.burt.jmespath.ast.FunctionCallNode;
import io.burt.jmespath.ast.CurrentNodeNode;
import io.burt.jmespath.ast.ComparisonNode;
import io.burt.jmespath.ast.RawStringNode;
import io.burt.jmespath.ast.AndNode;
import io.burt.jmespath.ast.OrNode;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.hasEntry;

public class AstGeneratorTest {
  @Test
  public void identifierExpression() throws IOException {
    Query expected = new Query(new FieldNode("foo"));
    Query actual = AstGenerator.fromString("foo");
    assertThat(actual, is(expected));
  }

  @Test
  public void chainExpression() throws IOException {
    Query expected = new Query(new ChainNode(new FieldNode("foo"), new FieldNode("bar")));
    Query actual = AstGenerator.fromString("foo.bar");
    assertThat(actual, is(expected));
  }

  @Test
  public void longChainExpression() throws IOException {
    Query expected = new Query(new ChainNode(new ChainNode(new ChainNode(new FieldNode("foo"), new FieldNode("bar")), new FieldNode("baz")), new FieldNode("qux")));
    Query actual = AstGenerator.fromString("foo.bar.baz.qux");
    assertThat(actual, is(expected));
  }

  @Test
  public void pipeExpression() throws IOException {
    Query expected = new Query(new PipeNode(new FieldNode("foo"), new FieldNode("bar")));
    Query actual = AstGenerator.fromString("foo | bar");
    assertThat(actual, is(expected));
  }

  @Test
  public void longPipeExpression() throws IOException {
    Query expected = new Query(new PipeNode(new PipeNode(new PipeNode(new FieldNode("foo"), new FieldNode("bar")), new FieldNode("baz")), new FieldNode("qux")));
    Query actual = AstGenerator.fromString("foo | bar | baz | qux");
    assertThat(actual, is(expected));
  }

  @Test
  public void indexExpression() throws IOException {
    Query expected = new Query(new SequenceNode(new FieldNode("foo"), new IndexNode(3)));
    Query actual = AstGenerator.fromString("foo[3]");
    assertThat(actual, is(expected));
  }

  @Test
  public void sliceExpression() throws IOException {
    Query expected = new Query(new SequenceNode(new FieldNode("foo"), new SliceNode(3, 4, 1)));
    Query actual = AstGenerator.fromString("foo[3:4]");
    assertThat(actual, is(expected));
  }

  @Test
  public void sliceWithoutStopExpression() throws IOException {
    Query expected = new Query(new SequenceNode(new FieldNode("foo"), new SliceNode(3, -1, 1)));
    Query actual = AstGenerator.fromString("foo[3:]");
    assertThat(actual, is(expected));
  }

  @Test
  public void sliceWithoutStartExpression() throws IOException {
    Query expected = new Query(new SequenceNode(new FieldNode("foo"), new SliceNode(0, 4, 1)));
    Query actual = AstGenerator.fromString("foo[:4]");
    assertThat(actual, is(expected));
  }

  @Test
  public void sliceWithStepExpression() throws IOException {
    Query expected = new Query(new SequenceNode(new FieldNode("foo"), new SliceNode(3, 4, 5)));
    Query actual = AstGenerator.fromString("foo[3:4:5]");
    assertThat(actual, is(expected));
  }

  @Test
  public void sliceWithStepButWithoutStopExpression() throws IOException {
    Query expected = new Query(new SequenceNode(new FieldNode("foo"), new SliceNode(3, -1, 5)));
    Query actual = AstGenerator.fromString("foo[3::5]");
    assertThat(actual, is(expected));
  }

  @Test
  public void sliceWithJustColonExpression() throws IOException {
    Query expected = new Query(new SequenceNode(new FieldNode("foo"), new SliceNode(0, -1, 1)));
    Query actual = AstGenerator.fromString("foo[:]");
    assertThat(actual, is(expected));
  }

  @Test
  public void sliceWithJustTwoColonsExpression() throws IOException {
    Query expected = new Query(new SequenceNode(new FieldNode("foo"), new SliceNode(0, -1, 1)));
    Query actual = AstGenerator.fromString("foo[::]");
    assertThat(actual, is(expected));
  }

  @Test
  public void flattenExpression() throws IOException {
    Query expected = new Query(new SequenceNode(new FieldNode("foo"), new FlattenNode()));
    Query actual = AstGenerator.fromString("foo[]");
    assertThat(actual, is(expected));
  }

  @Test
  public void bareFlattenExpression() throws IOException {
    Query expected = new Query(new FlattenNode());
    Query actual = AstGenerator.fromString("[]");
    assertThat(actual, is(expected));
  }

  @Test
  public void listWildcardExpression() throws IOException {
    Query expected = new Query(new SequenceNode(new FieldNode("foo"), new ListWildcardNode()));
    Query actual = AstGenerator.fromString("foo[*]");
    assertThat(actual, is(expected));
  }

  @Test
  public void bareListWildcardExpression() throws IOException {
    Query expected = new Query(new ListWildcardNode());
    Query actual = AstGenerator.fromString("[*]");
    assertThat(actual, is(expected));
  }

  @Test
  public void hashWildcardExpression() throws IOException {
    Query expected = new Query(new SequenceNode(new FieldNode("foo"), new HashWildcardNode()));
    Query actual = AstGenerator.fromString("foo.*");
    assertThat(actual, is(expected));
  }

  @Test
  public void bareHashWildcardExpression() throws IOException {
    Query expected = new Query(new HashWildcardNode());
    Query actual = AstGenerator.fromString("*");
    assertThat(actual, is(expected));
  }

  @Test
  public void currentNodeExpression() throws IOException {
    Query expected = new Query(new CurrentNodeNode());
    Query actual = AstGenerator.fromString("@");
    assertThat(actual, is(expected));
  }

  @Test
  public void selectionExpression() throws IOException {
    Query expected = new Query(new SequenceNode(new FieldNode("foo"), new SelectionNode(new FieldNode("bar"))));
    Query actual = AstGenerator.fromString("foo[?bar]");
    assertThat(actual, is(expected));
  }

  @Test
  public void selectionWithConditionExpression() throws IOException {
    Query expected = new Query(new SequenceNode(new FieldNode("foo"), new SelectionNode(new ComparisonNode("==", new FieldNode("bar"), new FieldNode("baz")))));
    Query actual = AstGenerator.fromString("foo[?bar == baz]");
    assertThat(actual, is(expected));
  }

  @Test
  public void simpleFunctionCallExpression() throws IOException {
    Query expected = new Query(new FunctionCallNode("foo"));
    Query actual = AstGenerator.fromString("foo()");
    assertThat(actual, is(expected));
  }

  @Test
  public void functionCallWithArgumentExpression() throws IOException {
    Query expected = new Query(new FunctionCallNode("foo", new FieldNode("bar")));
    Query actual = AstGenerator.fromString("foo(bar)");
    assertThat(actual, is(expected));
  }

  @Test
  public void functionCallWithMultipleArgumentsExpression() throws IOException {
    Query expected = new Query(new FunctionCallNode("foo", new FieldNode("bar"), new FieldNode("baz"), new CurrentNodeNode()));
    Query actual = AstGenerator.fromString("foo(bar, baz, @)");
    assertThat(actual, is(expected));
  }

  @Test
  public void bareRawStringExpression() throws IOException {
    Query expected = new Query(new RawStringNode("foo"));
    Query actual = AstGenerator.fromString("'foo'");
    assertThat(actual, is(expected));
  }

  @Test
  public void rawStringComparisonExpression() throws IOException {
    Query expected = new Query(new SequenceNode(new FieldNode("foo"), new SelectionNode(new ComparisonNode("!=", new FieldNode("bar"), new RawStringNode("baz")))));
    Query actual = AstGenerator.fromString("foo[?bar != 'baz']");
    assertThat(actual, is(expected));
  }

  @Test
  public void andExpression() throws IOException {
    Query expected = new Query(new AndNode(new FieldNode("foo"), new FieldNode("bar")));
    Query actual = AstGenerator.fromString("foo && bar");
    assertThat(actual, is(expected));
  }

  @Test
  public void orExpression() throws IOException {
    Query expected = new Query(new OrNode(new FieldNode("foo"), new FieldNode("bar")));
    Query actual = AstGenerator.fromString("foo || bar");
    assertThat(actual, is(expected));
  }

  @Test
  public void booleanComparisonExpression() throws IOException {
    Query expected = new Query(
      new SequenceNode(
        new FieldNode("foo"),
        new SelectionNode(
          new OrNode(
            new AndNode(
              new ComparisonNode("!=", new FieldNode("bar"), new RawStringNode("baz")),
              new ComparisonNode("==", new FieldNode("qux"), new RawStringNode("fux"))
            ),
            new ComparisonNode(">", new FieldNode("mux"), new RawStringNode("lux"))
          )
        )
      )
    );
    Query actual = AstGenerator.fromString("foo[?bar != 'baz' && qux == 'fux' || mux > 'lux']");
    assertThat(actual, is(expected));
  }

  @Test
  public void chainPipeIndexSliceCombination() throws IOException {
    Query expected = new Query(
      new PipeNode(
        new ChainNode(
          new SequenceNode(new FieldNode("foo"), new IndexNode(3)),
          new FieldNode("bar")
        ),
        new SequenceNode(
          new ChainNode(
            new FieldNode("baz"),
            new FieldNode("qux")
          ),
          new SliceNode(2, 3, 1)
        )
      )
    );
    Query actual = AstGenerator.fromString("foo[3].bar | baz.qux[2:3]");
    assertThat(actual, is(expected));
  }
}
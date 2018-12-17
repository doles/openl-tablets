package org.openl.binding.impl;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;

import org.openl.binding.IBoundNode;
import org.openl.binding.ILocalVar;
import org.openl.syntax.ISyntaxNode;
import org.openl.types.IOpenClass;
import org.openl.types.java.JavaArrayAggregateInfo;
import org.openl.vm.IRuntimeEnv;

class TransformIndexNode extends ABoundNode {
    private ILocalVar tempVar;
    private IBoundNode transformer;
    private IBoundNode targetNode;
    private Object[] resultPrototype;
    private IOpenClass resultType;

    TransformIndexNode(ISyntaxNode syntaxNode,
            IBoundNode targetNode,
            IBoundNode transformer,
            ILocalVar tempVar) {
        super(syntaxNode, targetNode, transformer);
        this.tempVar = tempVar;
        this.targetNode = targetNode;
        this.transformer = transformer;
        IOpenClass componentType = transformer.getType();
        this.resultPrototype = (Object[]) Array.newInstance(componentType.getInstanceClass(), 0);
        this.resultType = JavaArrayAggregateInfo.ARRAY_AGGREGATE.getIndexedAggregateType(componentType, 1);
    }

    @Override
    protected Object evaluateRuntime(IRuntimeEnv env) {
        Iterator<Object> elementsIterator = targetNode.getType().getAggregateInfo().getIterator(
            targetNode.evaluate(env));
        ArrayList<Object> result = new ArrayList<>();
        while (elementsIterator.hasNext()) {
            Object element = elementsIterator.next();
            tempVar.set(null, element, env);
            Object transformed = transformer.evaluate(env);
            result.add(transformed);
        }
        return result.toArray(resultPrototype);
    }

    public IOpenClass getType() {
        return resultType;
    }
}

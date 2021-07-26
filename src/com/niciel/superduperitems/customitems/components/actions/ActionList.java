package com.niciel.superduperitems.customitems.components.actions;

import com.niciel.superduperitems.SDIPlugin;
import com.niciel.superduperitems.cfg.Cfg;


import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

public class ActionList {


    @Cfg(path = "actions")
    public List<ICustomItemAction> actionList = new ArrayList() ;


    public List<ActionPasser> actions = new ArrayList<>();


    public ActionList() {

    }

    public void pass(Object... objc) {
        for (ActionPasser p : actions) {
            p.pass(objc);
        }
    }

    private Class[] parameters ;


    public void enable(Class... p) {
        this.parameters = p;
        itemaction :
        for (ICustomItemAction a : actionList) {

            ActionAcceptor acceptor;
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            for (Method m : a.getClass().getDeclaredMethods()) {
                acceptor = m.getAnnotation(ActionAcceptor.class);
                if (acceptor == null)
                    continue;
                if (! m.isAccessible())
                    m.setAccessible(true);

                Parameter[] params = m.getParameters();
                int[] positions = new int[params.length];
                for (int parPos = 0 ; parPos < params.length ; parPos++) {
                    int passPos = -1;
                    for (int pos = 0; pos < parameters.length ; pos++) {
                        if (parameters[pos].isAssignableFrom(params[parPos].getType())) {
                            passPos = pos;
                            break;
                        }
                    }
                    if (passPos < 0) {
                        SDIPlugin.instance.logWarning(this , " nie dodano, nie zapewnia parametru " + params[parPos]);
                        continue itemaction;
                    }
                    positions[parPos] = passPos;
                }

                ActionPasser passer = new ActionPasser();
                try {
                    passer.handle = lookup.unreflect(m);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                passer.object = a;
                passer.parameterToPassPosition = positions;
                actions.add(passer);
                continue itemaction;
            }
        }
    }



    public class ActionPasser {

        public int[] parameterToPassPosition;
        Object object;
        MethodHandle handle;


        public void pass(Object[] params) {
            Object[] invoke = new Object[parameterToPassPosition.length+1];
            invoke[0] = object;
            for (int i = 0 ; i < parameterToPassPosition.length ; i++) {
                invoke[i+1] = params[parameterToPassPosition[i]];
            }
            try {
                handle.invoke(invoke);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }

}

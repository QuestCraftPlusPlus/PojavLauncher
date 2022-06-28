package net.kdt.pojavlaunch.input.actions;

import net.kdt.pojavlaunch.openxr.OpenXRInstance;
import net.kdt.pojavlaunch.openxr.XrException;
import org.lwjgl.openxr.XR10;
import org.lwjgl.openxr.XrAction;
import org.lwjgl.openxr.XrActionSet;

public abstract class Action implements AutoCloseable {

    protected XrAction handle;
    public final String name;
    public final int type;

    public Action(String name, int type) {
        this.name = name;
        this.type = type;
    }

    public abstract void createHandle(XrActionSet actionSet, OpenXRInstance instance) throws XrException;

    public XrAction getHandle() {
        return handle;
    }

    public void destroyHandle() {
        XR10.xrDestroyAction(handle);
        if (this instanceof SessionAwareAction) {
            SessionAwareAction saa = (SessionAwareAction) this;
            saa.destroyHandleSession();
        }
    }

    @Override
    public void close() {
        destroyHandle();
    }
}

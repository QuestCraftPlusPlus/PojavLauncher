package net.kdt.pojavlaunch.input.actions;

import net.kdt.pojavlaunch.openxr.OpenXRInstance;
import net.kdt.pojavlaunch.openxr.XrException;
import org.lwjgl.PointerBuffer;
import org.lwjgl.openxr.*;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.system.MemoryStack.stackMallocPointer;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memUTF8;

public abstract class SingleInputAction<T> extends Action implements InputAction {

    protected static final XrActionStateGetInfo getInfo = XrActionStateGetInfo.calloc().type(XR10.XR_TYPE_ACTION_STATE_GET_INFO);

    public T currentState;
    public boolean changedSinceLastSync;
    public long lastChangeTime;
    public boolean isActive;

    public SingleInputAction(String name, int type) {
        super(name, type);
    }

    @Override
    public void createHandle(XrActionSet actionSet, OpenXRInstance instance) throws XrException {
        try (MemoryStack stack = stackPush()) {
            String localizedName = "mcxr.action." + this.name;

            XrActionCreateInfo actionCreateInfo = XrActionCreateInfo.malloc(stack).set(
                    XR10.XR_TYPE_ACTION_CREATE_INFO,
                    NULL,
                    memUTF8("mcxr." + this.name),
                    type,
                    0,
                    null,
                    memUTF8(localizedName)
            );
            PointerBuffer pp = stackMallocPointer(1);
            instance.check(XR10.xrCreateAction(actionSet, actionCreateInfo, pp), "xrCreateAction");
            handle = new XrAction(pp.get(), actionSet);
        }
    }
}

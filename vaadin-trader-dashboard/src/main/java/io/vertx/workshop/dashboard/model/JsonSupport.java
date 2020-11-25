package io.vertx.workshop.dashboard.model;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.Shareable;
import io.vertx.core.shareddata.impl.ClusterSerializable;

abstract class JsonSupport implements Shareable, ClusterSerializable, Serializable {

    protected final JsonObject json;

    protected JsonSupport(JsonObject json) {
        this.json = json;
    }

    @Override
    public void writeToBuffer(Buffer buffer) {
        json.writeToBuffer(buffer);
    }

    @Override
    public int readFromBuffer(int pos, Buffer buffer) {
        json.clear();
        return json.readFromBuffer(pos, buffer);
    }


    @Override
    public Shareable copy() {
        try {
            Constructor<? extends JsonSupport> ctor = getClass().getDeclaredConstructor(JsonObject.class);
            return ctor.newInstance(json);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return this;
    }

    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        Buffer buffer = Buffer.buffer();
        writeToBuffer(buffer);
        out.write(buffer.getBytes());
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        if (json == null) {
            try {
                Field field = JsonSupport.class.getDeclaredField("json");
                field.setAccessible(true);
                field.set(this, new JsonObject());
            } catch (Exception ex) {
                throw new IOException(ex);
            }
        }
        Buffer buffer = Buffer.buffer();
        byte[] bytes = new byte[8192];
        int read;
        while ((read = in.read(bytes, 0, 8192)) >= 0) {
            buffer.appendBytes(bytes, 0, read);
        }
        readFromBuffer(0, buffer);
    }

}

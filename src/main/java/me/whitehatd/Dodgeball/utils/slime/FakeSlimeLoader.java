package me.whitehatd.Dodgeball.utils.slime;

import com.grinderwolf.swm.internal.com.mongodb.MongoException;
import com.grinderwolf.swm.plugin.config.DatasourcesConfig;
import com.grinderwolf.swm.plugin.loaders.mongo.MongoLoader;

import java.io.IOException;

public class FakeSlimeLoader extends MongoLoader {

    public FakeSlimeLoader(DatasourcesConfig.MongoDBConfig config) throws MongoException {
        super(config);
    }

    @Override
    public void saveWorld(String worldName, byte[] serializedWorld, boolean lock) {
        // EMPTY BODY SO THAT WORLDS DON'T GET SAVED!
    }
}

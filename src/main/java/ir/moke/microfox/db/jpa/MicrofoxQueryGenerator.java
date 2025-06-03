package ir.moke.microfox.db.jpa;

import ir.moke.microfox.exception.MicrofoxException;
import org.hibernate.boot.MetadataSources;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;
import org.hibernate.tool.schema.TargetType;

import java.util.EnumSet;

import static ir.moke.microfox.db.jpa.MicroFoxJpa.getMetadataSources;

public class MicrofoxQueryGenerator {
    public static void updateSchema(String persistenceUnitName) {
        MetadataSources metadata = getMetadataSources(persistenceUnitName);
        if (metadata == null)
            throw new MicrofoxException("Persistence with name %s does not exists".formatted(persistenceUnitName));
        SchemaUpdate schemaUpdate = new SchemaUpdate();
        schemaUpdate.setHaltOnError(true);
        schemaUpdate.setFormat(false);
        schemaUpdate.setDelimiter(";");
        schemaUpdate.execute(EnumSet.of(TargetType.STDOUT), metadata.buildMetadata());
    }

    public static void createSchema(String persistenceUnitName) {
        MetadataSources metadata = getMetadataSources(persistenceUnitName);
        if (metadata == null)
            throw new MicrofoxException("Persistence with name %s does not exists".formatted(persistenceUnitName));
        SchemaExport schemaExport = new SchemaExport();
        schemaExport.setHaltOnError(true);
        schemaExport.setFormat(false);
        schemaExport.setDelimiter(";");
        schemaExport.execute(EnumSet.of(TargetType.STDOUT), SchemaExport.Action.CREATE, metadata.buildMetadata());
    }
}

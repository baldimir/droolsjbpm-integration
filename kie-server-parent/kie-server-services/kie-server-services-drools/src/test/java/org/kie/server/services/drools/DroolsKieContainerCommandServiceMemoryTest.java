package org.kie.server.services.drools;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.server.api.model.KieContainerResource;
import org.kie.server.services.impl.KieServerImpl;
import org.kie.server.services.impl.storage.file.KieServerStateFileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DroolsKieContainerCommandServiceMemoryTest {

    private static final Logger logger = LoggerFactory.getLogger(DroolsKieContainerCommandServiceMemoryTest.class);

    private static final File REPOSITORY_DIR = new File("target/repository-dir");
    private static final String KIE_SERVER_ID = "kie-server-impl-test";

    private static final String GROUP_ID = "com.agefos-pme.calculateur";
    private static final String ARTIFACT_ID = "moteur-regles";
    private static final String VERSION = "0.20.09";

    private static final String CONTAINER_ID = "calculateur";

    private KieServerImpl kieServer;

    @Before
    public void setupKieServerImpl() throws Exception {
        System.setProperty("org.kie.server.id", KIE_SERVER_ID);
        FileUtils.deleteDirectory(REPOSITORY_DIR);
        FileUtils.forceMkdir(REPOSITORY_DIR);
        kieServer = new KieServerImpl(new KieServerStateFileRepository(REPOSITORY_DIR));
    }

    @After
    public void cleanUp() {
        if (kieServer != null) {
            kieServer.destroy();
        }
    }

    @Test
    public void testScannerMemoryFootprint() {
        final KieServices kieServices = KieServices.Factory.get();
        final ReleaseId releaseId = kieServices.newReleaseId(GROUP_ID, ARTIFACT_ID, VERSION);
        kieServer.createContainer(CONTAINER_ID, new KieContainerResource(CONTAINER_ID, new org.kie.server.api.model.ReleaseId(releaseId)));
        measureMemoryFootprint(1000000, 1, 500, 30);
    }

    private void measureMemoryFootprint(final int numberOfIterations, final int numberOfAveragedIterations,
                                        final int acceptedNumberOfMemoryRaises, final long waitEachIterationMillis) {
        long lastTimeInMillis = System.currentTimeMillis();

        final Runtime runtime = Runtime.getRuntime();

        int memoryRaiseCount = 0;

        long averageMemory = 0;
        final List<Long> averageMemoryFootprints = new ArrayList<Long>();

        for (int i = 1; i < numberOfIterations; i++) {
            waitForMillis(waitEachIterationMillis, lastTimeInMillis);
            lastTimeInMillis = System.currentTimeMillis();

            final long usedMemory = runtime.totalMemory() - runtime.freeMemory();
            averageMemory = averageMemory + usedMemory;
            if ((i % numberOfAveragedIterations) == 0) {
                averageMemory = averageMemory / numberOfAveragedIterations;
                if (averageMemoryFootprints.size() > 0) {
                    final long previousAverageMemory = averageMemoryFootprints.get(averageMemoryFootprints.size() - 1);
                    if (averageMemory > previousAverageMemory) {
                        memoryRaiseCount++;
                    } else {
                        memoryRaiseCount = 0;
                    }
                    Assert.assertFalse(
                            "Memory raised during " + (acceptedNumberOfMemoryRaises + 1)
                                    + " consecutive measurements, there is probably some memory leak! "
                                    + getMemoryMeasurementsString(averageMemoryFootprints),
                            memoryRaiseCount > acceptedNumberOfMemoryRaises);
                }
                logger.info("Average memory: " + averageMemory / 1024 / 1024 + " mb");
                printMetaspaceSize();
                averageMemoryFootprints.add(averageMemory);
                averageMemory = 0;
            }
            updateArtifact(i);
        }
    }

    private void updateArtifact(final int version) {
        final KieServices kieServices = KieServices.Factory.get();

        kieServer.disposeContainer(CONTAINER_ID);
        final ReleaseId releaseId = kieServices.newReleaseId(GROUP_ID, ARTIFACT_ID, "0.20." + StringUtils.leftPad(String.valueOf(version % 24), 2, '0'));
        kieServer.createContainer(CONTAINER_ID, new KieContainerResource(CONTAINER_ID, new org.kie.server.api.model.ReleaseId(releaseId)));
//        kieServer.updateContainerReleaseId(CONTAINER_ID, new org.kie.server.api.model.ReleaseId(releaseId));
    }

    private void printMetaspaceSize() {
        for (final MemoryPoolMXBean memoryMXBean : ManagementFactory.getMemoryPoolMXBeans()) {
            if ("Metaspace".equals(memoryMXBean.getName())) {
                logger.info("Metaspace size: " + memoryMXBean.getUsage().getUsed() / 1024 / 1024 + " mb");
            }
        }
    }

    private void waitForMillis(final long millis, final long startTimeMillis) {
        while ((System.currentTimeMillis() - startTimeMillis) < millis) {
            // do nothing - wait
        }
    }

    private String getMemoryMeasurementsString(final List<Long> memoryMeasurements) {
        final StringBuilder builder = new StringBuilder();
        builder.append("Measured used memory: ");
        for (int i = 1; i <= memoryMeasurements.size(); i++) {
            final Long measurement = memoryMeasurements.get(i - 1) / 1024 / 1024;
            builder.append(i).append(": ").append(measurement).append(" MB; ");
        }
        return builder.toString();
    }
}

package org.marketcetera.rpc;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Utility class to find available TCP ports.
 * Replacement for Spring's deprecated SocketUtils.
 */
public final class TestSocketUtils {

    private static final Random random = new Random();

    /**
     * Find an available TCP port in the given range.
     *
     * @param minPort the minimum port number
     * @param maxPort the maximum port number
     * @return an available port number
     * @throws IllegalStateException if no available port is found
     */
    public static int findAvailableTcpPort(int minPort, int maxPort) {
        int portRange = maxPort - minPort;
        SortedSet<Integer> ports = new TreeSet<>();
        
        // First, try a random port within the range
        int randomPort = minPort + random.nextInt(portRange + 1);
        if (isPortAvailable(randomPort)) {
            return randomPort;
        }
        ports.add(randomPort);
        
        // Try specific ports one by one
        for (int port = minPort; port <= maxPort; port++) {
            if (!ports.contains(port) && isPortAvailable(port)) {
                return port;
            }
            ports.add(port);
            
            // To avoid scanning the full range, check if we've tried enough ports
            if (ports.size() >= 100) {
                break;
            }
        }
        
        // If we get here, we've tried enough ports
        throw new IllegalStateException("Could not find an available TCP port in the range [" + 
                                       minPort + ", " + maxPort + "]");
    }

    /**
     * Determine if the given port is available.
     *
     * @param port the port to check
     * @return {@code true} if the port is available
     */
    private static boolean isPortAvailable(int port) {
        try (ServerSocket socket = new ServerSocket(port)) {
            socket.setReuseAddress(true);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    
    // Private constructor to prevent instantiation
    private TestSocketUtils() {
    }
}
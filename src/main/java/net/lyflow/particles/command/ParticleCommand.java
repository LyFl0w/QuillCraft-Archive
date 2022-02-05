package net.lyflow.particles.command;

import net.lyflow.particles.Particles;
import net.lyflow.particles.utils.CommandUtils;
import net.lyflow.particles.utils.VectorUtils;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;

public class ParticleCommand implements CommandExecutor, TabCompleter{

    private final String[] subArgs = {"circle", "spincircle", "reversspincircle", "circlearound", "tempcircle"};

    private final Particles particles;
    public ParticleCommand(Particles particles){
        this.particles = particles;
    }

    @Override
    public boolean onCommand(@Nonnull CommandSender commandSender, @Nonnull Command command, @Nonnull String label, @Nonnull String[] args){
        if(commandSender instanceof final Player player){

            if(args.length != 2) return false;

            // Specific laser
            final String sub = args[0];

            // Number of points in each circle
            final int circlePoints = Integer.parseInt(args[1]);

            if(sub.equalsIgnoreCase(subArgs[0])){

                new BukkitRunnable(){
                    // radius of the circle
                    final double radius = 2.0d;
                    // Starting location for the first circle will be the player's eye location
                    final Location playerLoc = player.getEyeLocation();
                    // We need world for the spawnParticle function
                    final World world = playerLoc.getWorld();
                    // This is the direction the player is looking, normalized to a length (speed) of 1.
                    final Vector dir = player.getLocation().getDirection().normalize();

                    // We need the pitch in radians for the rotate axis function
                    // We also add 90 degrees to compensate for the non-standard use of pitch degrees in Minecraft.
                    //final double pitch = (playerLoc.getPitch() + 90.0F) * 0.017453292F;
                    final double pitch = Math.toRadians(playerLoc.getPitch()+90.0F);
                    // The yaw is also converted to radians here, but we need to negate it for the function to work properly
                    //final double yaw = -playerLoc.getYaw() * 0.017453292F;
                    final double yaw = Math.toRadians(-playerLoc.getYaw());

                    // This is the distance between each point around the circumference of the circle.
                    final double increment = (2*Math.PI)/circlePoints;
                    // Max length of the beam..for now
                    int beamLength = 30;

                    @Override
                    public void run(){
                        beamLength--;
                        if(beamLength < 1){
                            this.cancel();
                            return;
                        }
                        // We need to loop to get all of the points on the circle every loop
                        for(int i = 0; i < circlePoints; i++){
                            final double angle = i*increment;
                            final double x = radius*Math.cos(angle);
                            final double z = radius*Math.sin(angle);

                            // Convert that to a 3D Vector where the height is always 0
                            final Vector vec = new Vector(x, 0, z);

                            // Now rotate the circle point so it's properly aligned no matter where the player is looking:
                            VectorUtils.rotateAroundAxisX(vec, pitch);
                            VectorUtils.rotateAroundAxisY(vec, yaw);

                            // Add that vector to the player's current location
                            Objects.requireNonNull(world).spawnParticle(Particle.FLAME, playerLoc.clone().add(vec), 0); // Reminder to self - the "data" option for a (particle, location, data) is speed, not count!!
                        }
                /* We multiplied this by 1 already (using normalize()), ensuring the beam will
                   travel one block away from the player each loop.
                 */
                        playerLoc.add(dir);
                    }
                }.runTaskTimer(particles, 0, 1);

                return true;
            }

            if(sub.equalsIgnoreCase(subArgs[1])){
                new BukkitRunnable() {
                    // radius of the circle
                    double radius = 2.0d;
                    // Starting location for the first circle will be the player's eye location
                    final Location playerLoc = player.getEyeLocation();
                    // We need world for the spawnParticle function
                    final World world = playerLoc.getWorld();
                    // This is the direction the player is looking, normalized to a length (speed) of 1.
                    final Vector dir = player.getLocation().getDirection().normalize();
                    // We need the pitch in radians for the rotate axis function
                    // We also add 90 degrees to compensate for the non-standard use of pitch degrees in Minecraft.
                    final double pitch = (playerLoc.getPitch() + 90.0F) * 0.017453292F;
                    // The yaw is also converted to radians here, but we need to negate it for the function to work properly
                    final double yaw = -playerLoc.getYaw() * 0.017453292F;
                    // This is the distance between each point around the circumference of the circle.
                    final double increment = (2 * Math.PI) / circlePoints;
                    // This is used to rotate the circle as the beam progresses
                    double circlePointOffset = 0;
                    // Max length of the beam..for now
                    int beamLength = 30;
                    // This is the amount we will shrink the circle radius with each loop
                    final double radiusShrinkage = radius / (double) ((beamLength + 2) / 2);
                    @Override
                    public void run() {
                        // We need to loop to get all of the points on the circle every loop
                        for (int i = 0; i < circlePoints; i++) {
                            System.out.println(i);
                            // Angle on the circle + the offset for rotating each loop
                            final double angle = i * increment + circlePointOffset;
                            final double x = radius * Math.cos(angle);
                            final double z = radius * Math.sin(angle);
                            // Convert that to a 3D Vector where the height is always 0
                            final Vector vec = new Vector(x, 0, z);
                            // Now rotate the circle point so it's properly aligned no matter where the player is looking:
                            VectorUtils.rotateAroundAxisX(vec, pitch);
                            VectorUtils.rotateAroundAxisY(vec, yaw);
                            // Display the particle
                            Objects.requireNonNull(world).spawnParticle(Particle.FLAME, playerLoc.clone().add(vec), 0); // Reminder to self - the "data" option for a (particle, location, data) is speed, not count!!
                        }
                        /* Rotate the circle points each iteration, like rifling in a gun barrel
                        Since the particles in the circle are separated by "increment" distance, we rotate
                        the circle points 1/3 of increment with each loop. That means on the 3rd rotation,
                        the offset calculation matches the full increment, so we reset the offset to 0
                        instead of continuing to increase the offset.

                        For fun, comment out the if condition here and see what happens to the particles
                        without it. Since circlePointOffset is added to the angle in the calculation
                        above, it has an interesting side effect. Try it!
                        */
                        circlePointOffset += increment / 3;
                        // If the rotation matches the increment, reset to 0 to ensure a smooth rotation.
                        if (circlePointOffset >= increment) {
                            circlePointOffset = 0;
                        }
                        // Shrink each circle radius until it's just a point at the end of a long swirling cone
                        radius -= radiusShrinkage;
                        if (radius < 0) {
                            this.cancel();
                            return;
                        }

                        beamLength--;
                        if(beamLength < 1){
                            this.cancel();
                            return;
                        }

                        /* We multiplied this by 1 already (using normalize()), ensuring the beam will
                        travel one block away from the player each loop.
                        */
                        playerLoc.add(dir);
                    }
                }.runTaskTimer(particles, 0, 1);
                return true;
            }

            if(sub.equalsIgnoreCase(subArgs[2])){
                new BukkitRunnable() {
                    // radius of the circle
                    double radius = 0.0d;
                    // Starting location for the first circle will be the player's eye location
                    final Location playerLoc = player.getEyeLocation();
                    // We need world for the spawnParticle function
                    final World world = playerLoc.getWorld();
                    // This is the direction the player is looking, normalized to a length (speed) of 1.
                    final Vector dir = player.getLocation().getDirection().normalize();
                    // We need the pitch in radians for the rotate axis function
                    // We also add 90 degrees to compensate for the non-standard use of pitch degrees in Minecraft.
                    final double pitch = (playerLoc.getPitch() + 90.0F) * 0.017453292F;
                    // The yaw is also converted to radians here, but we need to negate it for the function to work properly
                    final double yaw = -playerLoc.getYaw() * 0.017453292F;
                    // This is the distance between each point around the circumference of the circle.
                    final double increment = (2 * Math.PI) / circlePoints;
                    // Max length of the beam..for now
                    int beamLength = 10;
                    @Override
                    public void run() {
                        // We need to loop to get all of the points on the circle every loop
                        for (int i = 0; i < circlePoints; i++) {
                            // Angle on the circle + the offset for rotating each loop
                            final double angle = i * increment;
                            final double x = radius * Math.cos(angle);
                            final double z = radius * Math.sin(angle);
                            // Convert that to a 3D Vector where the height is always 0
                            final Vector vec = new Vector(x, 0, z);
                            // Now rotate the circle point so it's properly aligned no matter where the player is looking:
                            VectorUtils.rotateAroundAxisX(vec, pitch);
                            VectorUtils.rotateAroundAxisY(vec, yaw);
                            // Display the particle
                            Objects.requireNonNull(world).spawnParticle(Particle.FLAME, playerLoc.clone().add(vec), 0); // Reminder to self - the "data" option for a (particle, location, data) is speed, not count!!
                        }

                        /* We multiplied this by 1 already (using normalize()), ensuring the beam will
                        travel one block away from the player each loop.
                        */
                        playerLoc.add(dir);

                        radius += increment * 10;

                        beamLength--;
                        if(beamLength < 1){
                            this.cancel();
                        }
                    }
                }.runTaskTimer(particles, 0, 1);
                return true;
            }

            if(sub.equalsIgnoreCase(subArgs[3])){
                new BukkitRunnable(){

                    final double radius = 1.0d;
                    final double increment = (Math.PI*2)/circlePoints;
                    final double speed = 0.1d/2;
                    double rotation = 0.0d;

                    double y = 0.0d;
                    boolean isDown = false;
                    boolean drawCircle = false;

                    final double defaultTimeToWaitCircle = 20L * circlePoints ;
                    double timeToWaitCircle = defaultTimeToWaitCircle;
                    long finalTimeToWait = 20L*60;

                    @Override
                    public void run(){

                        final Location playerLocation = player.getLocation();
                        final World world = playerLocation.getWorld();

                        if(!drawCircle){

                            if(isDown){
                                y -= 0.1;
                                if(y <= 0.0d){
                                    y = 0.0d;
                                    isDown = false;
                                    drawCircle = true;
                                }
                            }else{
                                y += 0.1;
                                if(y >= 2.0d){
                                    y = 2.0d;
                                    isDown = true;
                                    drawCircle = true;
                                }

                            }

                        }

                        for(int i = 0; i < circlePoints; i++){
                            final double angle = i*increment+rotation;
                            final double x = radius*Math.cos(angle);
                            final double z = radius*Math.sin(angle);
                            final Vector vector = new Vector(x, y, z);

                            Objects.requireNonNull(world).spawnParticle(Particle.FLAME, playerLocation.clone().add(vector), 0); // Reminder to self - the "data" option for a (particle, location, data) is speed, not count!!
                        }

                        if(drawCircle){
                            timeToWaitCircle--;

                            if(timeToWaitCircle <= 0){
                                timeToWaitCircle = defaultTimeToWaitCircle;
                                drawCircle = false;
                            }
                        }

                        rotation += speed*increment;

                        if(finalTimeToWait-- == 0) cancel();

                    }
                }.runTaskTimer(particles, 0L, 1L);
                return true;
            }

            if(args[0].equalsIgnoreCase(subArgs[4])){
                new BukkitRunnable(){

                    final double radius = 2.0d;
                    final double increment = (Math.PI*2)/circlePoints;
                    final double speed = 0.1d/2;
                    double rotation = 0.0d;

                    double finalTimeToWait = 20L * circlePoints;

                    @Override
                    public void run(){

                        final Location playerLocation = player.getLocation();
                        final World world = playerLocation.getWorld();

                        for(int i = 0; i < circlePoints; i++){
                            final double angle = i*increment+rotation;
                            final double x = radius*Math.cos(angle);
                            final double z = radius*Math.sin(angle);
                            final Vector vector = new Vector(x, 0, z);

                            Objects.requireNonNull(world).spawnParticle(Particle.FLAME, playerLocation.clone().add(vector), 0); // Reminder to self - the "data" option for a (particle, location, data) is speed, not count!!
                        }

                        rotation += speed*increment;

                        finalTimeToWait--;
                        if(finalTimeToWait == 0) cancel();

                    }
                }.runTaskTimer(particles, 0L, 1L);
                return true;
            }

        }
        return false;
    }


    @Override
    public List<String> onTabComplete(@Nonnull CommandSender commandSender, @Nonnull Command command, @Nonnull String label, @Nonnull String[] args){
        if(commandSender instanceof Player){
            if(args.length == 1){
                if(args[0] == null) return null;
                return CommandUtils.completionTable(args[0], subArgs);
            }
        }
        return null;
    }
}
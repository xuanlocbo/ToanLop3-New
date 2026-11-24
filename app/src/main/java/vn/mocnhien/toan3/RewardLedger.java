package vn.mocnhien.toan3;

import java.util.*;

/** Native, deterministic one-time awards. Persist Snapshot.claims as one value;
 * derive balance from the claims, never from a separately editable counter. */
public final class RewardLedger {
    private final Map<String,Integer> rewards;
    private final Map<Integer,String> chests = new LinkedHashMap<>();

    public static final class Snapshot {
        public final Set<String> claims;
        public final int balance;
        private Snapshot(Set<String> claims, int balance) {
            this.claims = Collections.unmodifiableSet(new LinkedHashSet<>(claims));
            this.balance = balance;
        }
    }

    public static final class Award {
        public final Snapshot snapshot;
        public final int earned;
        public final Set<String> newClaims;
        private Award(Snapshot snapshot, int earned, Set<String> newClaims) {
            this.snapshot = snapshot;
            this.earned = earned;
            this.newClaims = Collections.unmodifiableSet(new LinkedHashSet<>(newClaims));
        }
    }

    public RewardLedger(Map<String,Integer> questionRewards) {
        Map<String,Integer> copy = new LinkedHashMap<>();
        for (Map.Entry<String,Integer> entry : questionRewards.entrySet()) {
            String id = entry.getKey();
            Integer n = entry.getValue();
            if (id == null || n == null || id.startsWith("CHEST.")
                    || !(id.startsWith("SGK.") || id.startsWith("VBT.") || id.startsWith("BONUS.")))
                throw new IllegalArgumentException("Unknown reward key");
            if (id.startsWith("BONUS.")) {
                if (!id.matches("BONUS\\.(2[6-9]|3[0-9]|4[0-4])\\.[12]")
                        || n != (id.endsWith(".1") ? 80 : 120))
                    throw new IllegalArgumentException("Invalid bonus reward: " + id);
            } else if (n != 0 && n != 10) {
                throw new IllegalArgumentException("Invalid ordinary reward: " + id);
            }
            if (n > 0) copy.put(id, n);
        }
        for (int lesson=26; lesson<=44; lesson++) {
            if (!copy.containsKey("BONUS." + lesson + ".1") ||
                !copy.containsKey("BONUS." + lesson + ".2"))
                throw new IllegalArgumentException("Missing treasure challenge: " + lesson);
            chests.put(lesson, "CHEST." + lesson);
        }
        this.rewards = Collections.unmodifiableMap(copy);
    }

    private void awardChests(Set<String> claims) {
        for (Map.Entry<Integer,String> entry : chests.entrySet()) {
            int l = entry.getKey();
            if (claims.contains("BONUS." + l + ".1") && claims.contains("BONUS." + l + ".2"))
                claims.add(entry.getValue());
        }
    }

    public Snapshot restore(Collection<String> storedClaims) {
        Set<String> claims = new LinkedHashSet<>();
        if (storedClaims != null) {
            for (String id : storedClaims) if (rewards.containsKey(id)) claims.add(id);
        }
        // A chest is derived from both earned challenges, never awarded directly.
        awardChests(claims);
        int total = 0;
        for (String id : claims) total += rewards.getOrDefault(id, 100);
        return new Snapshot(claims, total);
    }

    /**
     * Only call verifiedCorrect=true after the full grading rule succeeds.
     * Pass the latest committed snapshot, on a serialized state-update path.
     * Save the returned claims atomically before displaying earned rewards.
     * A failed save must leave the prior committed snapshot in use.
     */
    public Award claim(Snapshot prior, String questionId, boolean verifiedCorrect) {
        Snapshot base = restore(prior == null ? Collections.emptySet() : prior.claims);
        Set<String> next = new LinkedHashSet<>(base.claims);
        if (verifiedCorrect && rewards.containsKey(questionId)) next.add(questionId);
        Snapshot updated = restore(next);
        Set<String> added = new LinkedHashSet<>(updated.claims);
        added.removeAll(base.claims);
        return new Award(updated, updated.balance - base.balance, added);
    }
}

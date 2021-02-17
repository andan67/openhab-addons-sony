package org.openhab.binding.sony.internal;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.openhab.core.types.StateOption;

class SonyUtilTest {

    @Test
    public void toBooleanObjectTest() {
        assertTrue(SonyUtil.toBooleanObject("true"));
        assertFalse(SonyUtil.toBooleanObject("false"));
        assertTrue(!Boolean.FALSE.equals(SonyUtil.toBooleanObject("true")));
        assertFalse(!Boolean.FALSE.equals(SonyUtil.toBooleanObject("false")));
        assertTrue(!Boolean.FALSE.equals(SonyUtil.toBooleanObject(null)));
    }

    @Test
    public void csvScannerTest() {
        String testCSV = "Source, DispNum, Title, Uri, Rank\n"
                + "\"tv:dvbt\",\"001\",\"Das Erste\",\"tv:dvbt?trip=8468.38912.64&srvName=Das%20Erste\",\"0\"\n"
                + "\"tv:dvbt\",\"002\",\"ZDF\",\"tv:dvbt?trip=8468.514.514&srvName=ZDF\",\"0\"\n"
                + "\"tv:dvbt\",003, \"3sat\",\"tv:dvbt?trip=8468.514.515&srvName=3sat\",\"0\"";

        Scanner scanner;
        String regex = "(?:,|\\n|^)(\"(?:(?:\"\")*[^\"]*)*\"|[^\",\\n]*|(?:\\n|$))";
        String regex2 = ",(?=([^\"]*\"[^\"]*\")*[^\"]*$)";
        String regex3 = "^\"|\"$";

        try {
            scanner = new Scanner(testCSV);
            scanner.nextLine();
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] values = line.split(regex2);
                Arrays.stream(values).forEach(v -> System.out.println(v.trim().replaceAll(regex3, "")));
            }
        } catch (Exception ex) {
            System.out.printf("Exception %s", ex.getMessage());
        }
    }

    @Test
    public void presetFavoriteTest() {
        String initialCSV = "Source,DispNum,Title,Uri,Rank\n"
                + "\"tv:dvbs\",\"100\",\"Sky 1 HD\",\"tv:dvbs?trip=133.8.147&srvName=Sky%201%20HD\",\"0\"\n"
                + "\"tv:dvbs\",\"101\",\"Sky Serien & Shows HD\",\"tv:dvbs?trip=133.4.117&srvName=Sky%20Serien%20%26%20Shows%20HD\",\"3\"\n"
                + "\"tv:dvbs\",\"103\",\"Sky Atlantic HD\",\"tv:dvbs?trip=133.13.110&srvName=Sky%20Atlantic%20HD\",\"2\"\n"
                + "\"tv:dvbs\",\"104\",\"Fox HD\",\"tv:dvbs?trip=133.10.124&srvName=Fox%20HD\",\"0\"\n"
                + "\"tv:dvbs\",\"105\",\"TNT Serie HD\",\"tv:dvbs?trip=133.9.123&srvName=TNT%20Serie%20HD\",\"1\"\n"
                + "\"tv:dvbs\",\"106\",\"TNT Comedy HD\",\"tv:dvbs?trip=133.14.136&srvName=TNT%20Comedy%20HD\",\"-1\"";

        String channelCSV = "Source,DispNum,Title,Uri,Rank\n"
                + "\"tv:dvbs\",\"100\",\"Sky 1 HD\",\"tv:dvbs?trip=133.8.147&srvName=Sky%201%20HD\",\"0\"\n"
                + "\"tv:dvbs\",\"101\",\"Sky Serien & Shows HD\",\"tv:dvbs?trip=133.4.117&srvName=Sky%20Serien%20%26%20Shows%20HD\",\"0\"\n"
                + "\"tv:dvbs\",\"104\",\"Fox HD\",\"tv:dvbs?trip=133.10.124&srvName=Fox%20HD\",\"0\"\n"
                + "\"tv:dvbs\",\"105\",\"TNT Serie HD\",\"tv:dvbs?trip=133.9.123&srvName=TNT%20Serie%20HD\",\"0\"\n"
                + "\"tv:dvbs\",\"106\",\"TNT Comedy HD\",\"tv:dvbs?trip=133.14.136&srvName=TNT%20Comedy%20HD\",\"0\"\n"
                + "\"tv:dvbs\",\"107\",\"SYFY HD\",\"tv:dvbs?trip=133.12.126&srvName=SYFY%20HD\",\"0\"\n"
                + "\"tv:dvbs\",\"108\",\"13th Street HD\",\"tv:dvbs?trip=133.13.127&srvName=13th%20Street%20HD\",\"0\"";

        HashMap<String, Integer> uriToRankMap = new HashMap<>();
        String regexCSV = ",(?=([^\"]*\"[^\"]*\")*[^\"]*$)";
        String regexQuotes = "^\"|\"$";
        Scanner scanner = new Scanner(initialCSV);
        scanner.nextLine();
        while (scanner.hasNextLine()) {
            try {
                final String line = scanner.nextLine();
                final String[] values = line.split(regexCSV);
                final String uri = values[3].trim().replaceAll(regexQuotes, "");
                final Integer rank = Integer.parseInt(values[4].trim().replaceAll(regexQuotes, ""));
                uriToRankMap.put(uri, rank);
            } catch (final Exception ex) {
                // ignore
            }
        }
        List<String> uriList = new ArrayList<>();
        List<StateOption> stateOptions = new ArrayList<>();
        scanner = new Scanner(channelCSV);
        scanner.nextLine();
        while (scanner.hasNextLine()) {
            try {
                final String line = scanner.nextLine();
                final String[] values = line.split(regexCSV);
                final String uri = values[3].trim().replaceAll(regexQuotes, "");
                final String title = values[2].trim().replaceAll(regexQuotes, "");
                uriList.add(uri);
                stateOptions.add(new StateOption(uri, title));
            } catch (final Exception ex) {
                // ignore
            }
        }

        System.out.println(uriToRankMap.size());
        List<String> filteredAndSortedList = uriList.stream()
                .filter(uri -> (uriToRankMap.getOrDefault(uri, Integer.MAX_VALUE)) >= 0)
                .sorted(Comparator.comparing(uri -> uriToRankMap.getOrDefault(uri, Integer.MAX_VALUE)))
                .collect(Collectors.toList());
        List<StateOption> stateOptionsFilteredAndSorted = stateOptions.stream()
                .filter(a -> (uriToRankMap.getOrDefault(a.getValue(), Integer.MAX_VALUE)) >= 0)
                // .sorted(Comparator.comparing(StateOption::getValue).thenComparing(StateOption::getLabel))
                // .sorted(Comparator.comparing(a -> uriToRankMap.getOrDefault(a.getValue(), Integer.MAX_VALUE))).
                // .sorted(Comparator
                // .<StateOption, Integer> comparing(
                // a -> uriToRankMap.getOrDefault(a.getValue(), Integer.MAX_VALUE))
                // .thenComparing(a -> SonyUtil.defaultIfEmpty(a.getLabel(), "")))
                .sorted(Comparator.<StateOption> comparingInt(a -> {
                    Integer r = uriToRankMap.getOrDefault(a.getValue(), 0);
                    return r == 0 ? Integer.MAX_VALUE : r;
                }).thenComparing(a -> SonyUtil.defaultIfEmpty(a.getLabel(), ""))).collect(Collectors.toList());
        System.out.println(filteredAndSortedList.size());
        System.out.println(stateOptionsFilteredAndSorted.size());
    }
}

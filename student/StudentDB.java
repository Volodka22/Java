package info.kgeorgiy.ja.smaglii.student;

import info.kgeorgiy.java.advanced.student.AdvancedQuery;
import info.kgeorgiy.java.advanced.student.Group;
import info.kgeorgiy.java.advanced.student.GroupName;
import info.kgeorgiy.java.advanced.student.Student;


import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class StudentDB implements AdvancedQuery {

    private final Comparator<Student> NAME_COMPARATOR = Comparator.comparing(Student::getLastName)
            .thenComparing(Student::getFirstName)
            .reversed()
            .thenComparingInt(Student::getId);


    @Override
    public List<String> getFirstNames(Collection<Student> students, int[] indices) {
        return getMappingList(students, Student::getFirstName, indices);
    }

    @Override
    public List<String> getLastNames(Collection<Student> students, int[] indices) {
        return getMappingList(students, Student::getLastName, indices);
    }

    @Override
    public List<GroupName> getGroups(Collection<Student> students, int[] indices) {
        return getMappingList(students, Student::getGroup, indices);
    }

    @Override
    public List<String> getFullNames(Collection<Student> students, int[] indices) {
        return getMappingList(students, student -> student.getFirstName() + " " + student.getLastName(), indices);
    }


    @Override
    public List<Group> getGroupsByName(Collection<Student> students) {
        return getGroupsList(students, NAME_COMPARATOR);
    }

    @Override
    public List<Group> getGroupsById(Collection<Student> students) {
        return getGroupsList(students, Comparator.comparing(Student::getId));
    }


    private <T> Stream<Map.Entry<T, List<Student>>> getGroupsStream(Collection<Student> students, Function<Student, T> grouping) {
        return students.stream()
                .sorted(Comparator.comparing(Student::getGroup))
                .collect(Collectors.groupingBy(grouping))
                .entrySet()
                .stream();
    }

    private List<Group> getGroupsList(Collection<Student> students, Comparator<Student> studentComparator) {
        return getGroupsStream(students, Student::getGroup)
                .map(s -> new Group(s.getKey(), s.getValue().stream().sorted(studentComparator).toList()))
                .sorted(Comparator.comparing(Group::getName)).toList();
    }

    private record GroupingCounter<T>(T name, int counter) {
    }

    private <K> K getLargest(Collection<Student> students,
                             Function<Student, K> key,
                             Comparator<GroupingCounter<K>> compKey,
                             ToIntFunction<List<Student>> size,
                             K defaultAns) {
        return getGroupsStream(students, key)
                .map(s -> new GroupingCounter<>(s.getKey(), size.applyAsInt(s.getValue())))
                .sorted(compKey)
                .max(Comparator.comparingInt(GroupingCounter::counter))
                .map(GroupingCounter::name)
                .orElse(defaultAns);
    }

    private <T> int distinctSize(List<Student> students, Function<Student, T> value) {
        return students.stream().map(value).sorted().distinct().toList().size();
    }

    @Override
    public String getMostPopularName(Collection<Student> students) {
        return getLargest(students,
                Student::getFirstName,
                Comparator.comparing(GroupingCounter<String>::name).reversed(),
                s -> distinctSize(s, Student::getGroup),
                "");
    }

    @Override
    public GroupName getLargestGroup(Collection<Student> students) {
        return getLargest(students,
                Student::getGroup,
                Comparator.comparing(GroupingCounter<GroupName>::name).reversed(),
                List::size,
                null);
    }

    @Override
    public GroupName getLargestGroupFirstName(Collection<Student> students) {
        return getLargest(students,
                Student::getGroup,
                Comparator.comparing(GroupingCounter<GroupName>::name),
                s -> distinctSize(s, Student::getFirstName),
                null);
    }

    private int[] getFullIndexes(int size) {
        return IntStream.range(0, size).toArray();
    }

    private <T> List<T> getMappingList(Collection<Student> students, Function<Student, T> function, int[] indies) {
        return getMappingList(students.stream().toList(), function, indies);
    }

    private <T> Stream<T> getMappingStream(List<Student> students, Function<Student, T> function, int[] indies) {
        return Arrays.stream(indies).mapToObj(i -> function.apply(students.get(i)));
    }

    private <T> List<T> getMappingList(List<Student> students, Function<Student, T> function, int[] indies) {
        return getMappingStream(students, function, indies).toList();
    }

    private <T> List<T> getMappingList(List<Student> students, Function<Student, T> function) {
        return getMappingList(students, function, getFullIndexes(students.size()));
    }

    private List<Student> sortBy(Collection<Student> students, Comparator<Student> comp) {
        return students.stream().sorted(comp).toList();
    }

    @Override
    public List<String> getFirstNames(List<Student> students) {
        return getMappingList(students, Student::getFirstName);
    }

    @Override
    public List<String> getLastNames(List<Student> students) {
        return getMappingList(students, Student::getLastName);
    }

    @Override
    public List<GroupName> getGroups(List<Student> students) {
        return getMappingList(students, Student::getGroup);
    }

    @Override
    public List<String> getFullNames(List<Student> students) {
        return getMappingList(students, student -> student.getFirstName() + " " + student.getLastName());
    }

    @Override
    public Set<String> getDistinctFirstNames(List<Student> students) {
        return getMappingStream(students, Student::getFirstName, getFullIndexes(students.size())).collect(Collectors.toSet());
    }

    @Override
    public String getMaxStudentFirstName(List<Student> students) {
        return students.stream().max(Comparator.comparingInt(Student::getId)).map(Student::getFirstName).orElse("");
    }

    @Override
    public List<Student> sortStudentsById(Collection<Student> students) {
        return sortBy(students, Comparator.comparingInt(Student::getId));
    }

    @Override
    public List<Student> sortStudentsByName(Collection<Student> students) {
        return sortBy(students, NAME_COMPARATOR);
    }

    private <U, T> Stream<T> getFilteredEqualsStream(Collection<T> collection, Function<T, U> getField, U filter) {
        return collection.stream().filter(student -> getField.apply(student).equals(filter));
    }

    private <U> List<Student> getFilteredEqualsSortedList(Collection<Student> collection, Function<Student, U> getField, U filter) {
        return getFilteredEqualsStream(collection, getField, filter).sorted(NAME_COMPARATOR).toList();
    }

    @Override
    public List<Student> findStudentsByFirstName(Collection<Student> students, String name) {
        return getFilteredEqualsSortedList(students, Student::getFirstName, name);
    }

    @Override
    public List<Student> findStudentsByLastName(Collection<Student> students, String name) {
        return getFilteredEqualsSortedList(students, Student::getLastName, name);
    }

    @Override
    public List<Student> findStudentsByGroup(Collection<Student> students, GroupName group) {
        return getFilteredEqualsSortedList(students, Student::getGroup, group);
    }

    @Override
    public Map<String, String> findStudentNamesByGroup(Collection<Student> students, GroupName group) {
        return getFilteredEqualsStream(students, Student::getGroup, group)
                .collect(Collectors
                        .toMap(Student::getLastName,
                                Student::getFirstName,
                                BinaryOperator.minBy(Comparator.naturalOrder())
                        )
                );
    }
}

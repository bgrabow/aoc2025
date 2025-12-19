use crate::util;
use std::iter;

pub enum Direction {
    Left,
    Right,
}

pub struct Rotation {
    pub direction: Direction,
    pub distance: i32,
}

fn rotations(input: &str) -> Vec<Rotation> {
    input
        .trim()
        .lines()
        .map(|s| {
            let (dir_char, dist_str) = s.split_at(1);
            let direction = match dir_char {
                "L" => Direction::Left,
                "R" => Direction::Right,
                _ => panic!("Invalid direction character"),
            };
            let distance: i32 = dist_str.parse().expect("Invalid distance number");
            Rotation {
                direction,
                distance,
            }
        })
        .collect()
}

pub fn parse_input() -> Vec<Rotation> {
    rotations(&util::file_to_string("resources/input_01.txt"))
}

fn cycle(value: i32, min: i32, max: i32) -> i32 {
    let range_size = max - min + 1;
    let mut adjusted_value = value - min;
    adjusted_value = adjusted_value % range_size;
    if adjusted_value < 0 {
        adjusted_value += range_size;
    }
    adjusted_value + min
}

const DIAL_START: i32 = 50;
const DIAL_MAX: i32 = 99;
const DIAL_MIN: i32 = 0;
const DIAL_RANGE: i32 = DIAL_MAX - DIAL_MIN + 1;

pub fn folds<Acc, Elem, F, I>(init: &Acc, f: F, coll: I) -> Vec<Acc>
where
    Acc: Clone,
    F: Fn(Acc, Elem) -> Acc,
    I: Iterator<Item = Elem>,
{
    let mut acc = init.clone();
    let first = iter::once(init.clone());

    first
        .chain(coll.map(|elem| {
            let new_acc = f(acc.clone(), elem);
            acc = new_acc;
            acc.clone()
        }))
        .collect::<Vec<Acc>>()
}

pub fn signed_distance(rot: &Rotation) -> i32 {
    match rot.direction {
        Direction::Left => -rot.distance,
        Direction::Right => rot.distance,
    }
}

pub fn solve_part1() -> String {
    let password_num = parse_input()
        .iter()
        .map(signed_distance)
        .fold(vec![DIAL_START], |acc, x| {
            let last = *acc.last().unwrap();
            let mut new_acc = acc;
            new_acc.push(cycle(last + x, DIAL_MIN, DIAL_MAX));
            new_acc
        })
        .iter()
        .filter(|&x| x == &0)
        .count();

    password_num.to_string()
}

pub fn zeroes_between(from: i32, to: i32) -> i32 {
    let cycles_between = (to - from) / DIAL_RANGE;
    let normalized_to = to - (cycles_between * DIAL_RANGE);
    let from_cycle = from.div_euclid(DIAL_RANGE);
    let to_cycle = to.div_euclid(DIAL_RANGE);
    let local_clicks = if normalized_to == from {
        0
    } else if normalized_to.rem_euclid(DIAL_RANGE) == DIAL_MIN {
        1
    } else if from_cycle != to_cycle {
        1
    } else {
        0
    };
    cycles_between.abs() + local_clicks
}

#[test]
fn zeroes_between_test() {
    assert_eq!(zeroes_between(0, 0), 0);
    assert_eq!(zeroes_between(10, 15), 0);
    assert_eq!(zeroes_between(-1, 0), 1);
    assert_eq!(zeroes_between(99, 0), 1);
    assert_eq!(zeroes_between(0, 1), 0);
    assert_eq!(zeroes_between(0, 100), 1);
    assert_eq!(zeroes_between(50, 100), 1);
    assert_eq!(zeroes_between(150, 300), 2);
    assert_eq!(zeroes_between(-150, -300), 2);
    assert_eq!(zeroes_between(-150, 0), 2);
    assert_eq!(zeroes_between(150, -300), 5);
    assert_eq!(zeroes_between(-150, 300), 5);
}

pub fn solve_part2() -> String {
    let steps = folds(
        &DIAL_START,
        |acc, x| acc + x,
        parse_input().iter().map(signed_distance),
    );

    let total_clicks = steps
        .windows(2)
        .map(|w| zeroes_between(w[0], w[1]))
        .fold(0, |a, b| a + b);

    let mut clicks = 0;
    let mut pos = DIAL_START;
    for d in parse_input().iter().map(signed_distance) {
        match d < 0 {
            true => for x in (pos + d)..pos {
                if x.rem_euclid(DIAL_RANGE) == 0 {
                    clicks += 1;
                }
            }
            false => for x in pos..(pos + d) {
                if x.rem_euclid(DIAL_RANGE) == 0 {
                    clicks += 1;
                }
            }
        }
        pos += d;
    }

    //clicks.to_string()
    total_clicks.to_string()
}

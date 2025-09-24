import numpy as np

def generate_binomial_trials_from_stats(mean, std, x):
    if std**2 == 0 or mean == 0:
        return None
    n = round(mean**2 / (mean - std**2))
    if n <= 0:
        return None

    p = mean / n
    if p < 0 or p > 1:
        return None

    return np.random.binomial(n, p, x).tolist()
def generate_uniform_trials(low, high, x):
    return np.random.randint(low, high + 1, x).tolist()

def discrete_distribution(num_trials):
    values = [5, 1, 0, 4, 2, 3, 3, 2, 4, 5, 2, 3, 4]
    probabilities = [0.13, 0.13, 0.13, 0.1, 0.1, 0.07, 0.07, 0.05, 0.05, 0.1, 0.02, 0.02, 0.03]

    return [int(x) for x in np.random.choice(values, size=num_trials, p=probabilities)]
binomial_trials = generate_binomial_trials_from_stats(3, 1, 30)
uniform_trials = generate_uniform_trials(1, 5, 30)
samples = discrete_distribution(30)

print("Binomial Trials:")
final=len(binomial_trials)
for i, val in enumerate(binomial_trials, 1):
    if i!=final:
        print(val, end=',')
    else:
        print(val)
print("Sum: " + str(sum(binomial_trials)))
print()
print("Uniform Trials:")
final1=len(uniform_trials)
for i, val in enumerate(uniform_trials, 1):
    if i!=final1:
        print(val, end=',')
    else:
        print(val)
print("Sum: " + str(sum(uniform_trials)))
print()
print("Custom Distribution:")
final2=len(samples)
for i, val in enumerate(samples, 1):
    if i!=final2:
        print(val, end=',')
    else:
        print(val)
print("Sum: " + str(sum(samples)))
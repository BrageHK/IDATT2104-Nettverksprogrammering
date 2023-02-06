#include <iostream>
#include <thread>
#include <mutex>
#include <condition_variable>
#include <queue>
#include <functional>

#include <unistd.h>

using namespace std;

class Workers {
    mutex wait_mutex;
    condition_variable condition;
    bool end = false;
    queue<function<void()>> tasks;
    thread t1;

    public:

    int num_threads;
    Workers(int num_threads) : num_threads(num_threads) {}
    vector<thread> threads;

    void start() {
        for(int i = 0; i < num_threads; i++) {
            threads.emplace_back([this] {
                while (true) {
                    function<void()> task;
                    {
                        unique_lock<mutex> lock(this->wait_mutex);
                        this->condition.wait(lock, [this] {
                            return this->end || !this->tasks.empty();
                        });
                        if (this->end && this->tasks.empty())
                            return;
                        task = move(this->tasks.front());
                        this->tasks.pop();
                    }
                    task();
                }
            });
        }
    }

    void stop() {
        {
            unique_lock<mutex> lock(mutex);
            end = true;
        }
        condition.notify_all();
        join();
    }

    void post(function<void()> task) {
        {
            unique_lock<mutex> lock(mutex);
            tasks.emplace(task);
        }
        condition.notify_one();
    }

    void join() {
        for(thread &thread : threads)
            thread.join();
    }
};

void print(string s) {
    cout << s;
}

int main() {

    mutex mutex_wait;

    Workers worker_threads(4);
    Workers event_loop(1);
    worker_threads.start(); // Create 4 internal threads
    event_loop.start(); // Create 1 internal thread
    worker_threads.post([] {
        // sleep for 1 second
        sleep(1);
        print("1");
    });

    worker_threads.post([] {
        sleep(0);
        print("2");
    });

    event_loop.post([] {
        sleep(1);
        print("3");
    });

    event_loop.post([] {
        print("4");
    });

    worker_threads.stop();
    event_loop.stop();

    return 0;
}

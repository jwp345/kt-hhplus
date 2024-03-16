import http from 'k6/http';
import {sleep, check} from 'k6';

export const options = {
    // see https://k6.io/docs/using-k6/options/#discard-response-bodies
    // discardResponseBodies: true,
    // see https://k6.io/docs/using-k6/options/#stages

    vus: 1000,
    duration: '1s'
    // stages: [
    //     {duration: '10s', target: 10} 
    // ],
    // see https://k6.io/docs/using-k6/options/#hosts
    // hosts: {
    //   'test.k6.io': '1.2.3.4',
    //   'test.k6.io:443': '1.2.3.4:8443',
    // },
    // hosts: null,
};
 
export default function() {
    const authHeader = {
        headers: {
            'X-WAIT-TOKEN': `rO0ABXNyACxjb20uaGhwbHVzLmluZnJhc3RydWN0dXJlLnNlY3VyaXR5LldhaXRUb2tlbudSlW4gg/ttAgADSgAIY3JlYXRlQXRKAAVvcmRlckoABHV1aWR4cAAAAABlrp43AAAAAAAAA6AAAAAAAAABqg==`,
        }
    }
    // login();
    // findSeat(authHeader);
    findByDate(authHeader);
    sleep(1);
}

function login() {
    let userId = __VU
    const url = 'http://localhost:8080/api/v1/token';
    const payload = JSON.stringify({
       uuid: userId
    });
 
    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };
    
    let response = http.post(url, payload, params);

    check(response, {
        'is status 200': (r) => r.status === 200,
      });

    // return {
    //     headers: {
    //         'X-WAIT-TOKEN': `${response.json('data.token')}`,
    //     },
    // };
}

function findSeat(authHeader) {
    const url = 'http://localhost:8080/api/v1/booking/dates/available?seatId=1';

    let response = http.get(url, authHeader);

    check(response, {
        'is status 200': (r) => r.status === 200,
    });
}

function findByDate(authHeader) {
    const dateParameter = '2024-10-25 23:28';
    const url = 'http://localhost:8080/api/v1/booking/seats/available?date=2024-10-25%2023%3A28';

    let response = http.get(url, authHeader);

    check(response, {
        'is status 200': (r) => r.status === 200,
    });
}
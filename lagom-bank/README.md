# lagom-bank

A simple bank account with the following requirements: 

- Users can open a bank account with starting non-negative balance
- Bank does not allow overdraft (negative balance)
- Transfers between accounts incur a flat transaction fee of $0.25
- Minimum amount of money to transfer is $5 (i.e. $5.25 with fee)
- Bank wants to know, "in real time", how much money it has made out of transaction fees. 

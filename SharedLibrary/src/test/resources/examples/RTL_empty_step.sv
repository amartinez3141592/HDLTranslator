module empty(
	input logic [2:0] a,
	input logic VCC,
	input logic GND,
	input logic clk,
	input logic reset,
	output logic [2:0] led
);
	logic next_isActivated;
	logic isActivated;
	typedef enum logic [2:0] {
		S0 = 3'b100,
		S1 = 3'b010,
		S2 = 3'b001
	} state_t;
	state_t next_state;
	state_t state;
	always_ff @(posedge clk or negedge reset) begin
		if (!reset) begin
			isActivated <= 1'b0;
			state <= S0;
		end else begin
			isActivated <= next_isActivated;
			state <= next_state;
		end
	end
	always_comb begin 
		next_state = state;
		next_isActivated = isActivated;
		led = 3'b000;
		case(state)
			S0: begin
				if (VCC) begin next_state = S1;
				end
			end
			S1: begin
				led={a[0],a[2],a[1]};
				if (VCC) begin next_state = S2;
				end
			end
			S2: begin
				led={a[1],a[0],a[2]};
				if (VCC) begin next_state = S0;
				end
			end
		endcase
	end
endmodule

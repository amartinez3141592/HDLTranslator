module empty(
	input logic [2:0] a,
	input logic clk,
	input logic reset,
	output logic [2:0] led
);
	typedef enum logic [2:0] {
		S0 = 3'b100,
		S1 = 3'b010,
		S2 = 3'b001
	} state_t;
	state_t next_state;
	state_t state;
	always_ff @( posedge clk or negedge reset ) begin
		if (!(reset)) begin
			state <= S0;
		end else begin
			state <= next_state;
		end
	end
	always_comb begin 
		next_state = state;
		led = 3'b000;
		case(state)
			S0: begin
				if (1) begin next_state = S1;
				end
			end
			S1: begin
				led={a[0],a[2],a[1]};
				if (1) begin next_state = S2;
				end
			end
			S2: begin
				led={a[1],a[0],a[2]};
				if (1) begin next_state = S0;
				end
			end
		endcase
	end
endmodule

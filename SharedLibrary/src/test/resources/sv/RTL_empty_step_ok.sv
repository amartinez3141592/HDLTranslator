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
	} step_t;
	step_t next_step;
	step_t step;
	always_ff @( posedge clk or negedge reset ) begin
		if (!(reset)) begin
			step <= S0;
		end else begin
			step <= next_step;
		end
	end
	always_comb begin 
		next_step = step;
		led = 3'b000;
		case(step)
			S0: begin
				if (1) begin next_step = S1;
				end
			end
			S1: begin
				led={a[0],a[2],a[1]};
				if (1) begin next_step = S2;
				end
			end
			S2: begin
				led={a[1],a[0],a[2]};
				if (1) begin next_step = S0;
				end
			end
		endcase
	end
endmodule

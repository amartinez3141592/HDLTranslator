module empty(
	input wire [2:0] a,
	input wire clk,
	input wire reset,
	output wire [2:0] led
);
	localparam
		S0 = 3'b100,
		S1 = 3'b010,
		S2 = 3'b001;
	reg [2:0] next_state;
	reg [2:0] state;
	always @(posedge clk or negedge reset) begin
		if (!reset) begin
			state <= S0;
		end else begin
			state <= next_state;
		end
	end
	always @(a or state) begin
		next_state = state;
		led = 3'b000;
		case(state)
			S0: begin
				if (1) begin
					next_state = S1;
				end
			end
			S1: begin
				led = {a[0],a[2],a[1]};
				if (1) begin
					next_state = S2;
				end
			end
			S2: begin
				led = {a[1],a[0],a[2]};
				if (1) begin
					next_state = S0;
				end
			end
		endcase
	end
endmodule